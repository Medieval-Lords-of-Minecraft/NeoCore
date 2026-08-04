package me.neoblade298.neocore.tools;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class BlockSpriteGenerator {
    private static final String MINECRAFT_VERSION = "26.2";
    private static final List<String> TEXTURE_KEYS = List.of(
        "all", "front", "side", "top", "up", "end", "layer0", "particle", "bottom");
    private static final Map<String, String> OVERRIDES = createOverrides();
    private static final Set<String> EXCLUSIONS = Set.of(
            "air", "barrier", "cave_air", "end_gateway", "end_portal", "light",
            "moving_piston", "structure_void", "void_air");

    private BlockSpriteGenerator() {
    }

    private static Map<String, String> createOverrides() {
        Map<String, String> overrides = new HashMap<>();
        overrides.put("attached_melon_stem", "minecraft:block/melon_stem_attached");
        overrides.put("attached_pumpkin_stem", "minecraft:block/pumpkin_stem_attached");
        overrides.put("bubble_column", "minecraft:block/water_still");
        overrides.put("chest", "minecraft:entity/chest/normal");
        overrides.put("copper_chest", "minecraft:entity/chest/copper");
        addHeadOverrides(overrides, "creeper_head", "minecraft:entity/creeper/creeper");
        addHeadOverrides(overrides, "dragon_head", "minecraft:entity/enderdragon/dragon");
        overrides.put("ender_chest", "minecraft:entity/chest/ender");
        overrides.put("exposed_copper_chest", "minecraft:entity/chest/copper_exposed");
        overrides.put("fire", "minecraft:block/fire_0");
        overrides.put("lava", "minecraft:block/lava_still");
        overrides.put("melon_stem", "minecraft:block/melon_stem");
        overrides.put("nether_portal", "minecraft:block/nether_portal");
        overrides.put("oxidized_copper_chest", "minecraft:entity/chest/copper_oxidized");
        addHeadOverrides(overrides, "piglin_head", "minecraft:entity/piglin/piglin");
        addHeadOverrides(overrides, "player_head", "minecraft:entity/player/wide/steve");
        overrides.put("pumpkin_stem", "minecraft:block/pumpkin_stem");
        overrides.put("redstone_wire", "minecraft:block/redstone_dust_dot");
        overrides.put("soul_fire", "minecraft:block/soul_fire_0");
        addHeadOverrides(overrides, "skeleton_skull", "minecraft:entity/skeleton/skeleton");
        overrides.put("trapped_chest", "minecraft:entity/chest/trapped");
        overrides.put("water", "minecraft:block/water_still");
        overrides.put("weathered_copper_chest", "minecraft:entity/chest/copper_weathered");
        overrides.put("wheat", "minecraft:block/wheat_stage7");
        overrides.put("waxed_copper_chest", "minecraft:entity/chest/copper");
        overrides.put("waxed_exposed_copper_chest", "minecraft:entity/chest/copper_exposed");
        overrides.put("waxed_oxidized_copper_chest", "minecraft:entity/chest/copper_oxidized");
        overrides.put("waxed_weathered_copper_chest", "minecraft:entity/chest/copper_weathered");
        addHeadOverrides(overrides, "wither_skeleton_skull", "minecraft:entity/skeleton/wither_skeleton");
        addHeadOverrides(overrides, "zombie_head", "minecraft:entity/zombie/zombie");

        for (String color : List.of("white", "orange", "magenta", "light_blue", "yellow", "lime", "pink",
                "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black")) {
            overrides.put(color + "_banner", "minecraft:block/" + color + "_wool");
            overrides.put(color + "_wall_banner", "minecraft:block/" + color + "_wool");
        }
        return Map.copyOf(overrides);
    }

    private static void addHeadOverrides(Map<String, String> overrides, String material, String texture) {
        overrides.put(material, texture);
        String wallMaterial = material.replace("_head", "_wall_head").replace("_skull", "_wall_skull");
        overrides.put(wallMaterial, texture);
    }

    public static void main(String[] args) throws Exception {
        Arguments arguments = Arguments.parse(args);
        Path clientJar = arguments.clientJar != null ? arguments.clientJar : defaultClientJar();
        if (!Files.isRegularFile(clientJar)) {
            throw new IllegalArgumentException("Minecraft " + MINECRAFT_VERSION + " client JAR not found: " + clientJar);
        }

        Result result;
        try (Assets assets = new Assets(clientJar)) {
            result = resolveBlocks(assets);
        }

        Path propertiesPath = arguments.repo.resolve(
                "src/me/neoblade298/neocore/bukkit/util/vanilla-block-sprites.properties");
        Path reportPath = arguments.repo.resolve("tools/vanilla-block-sprites-report.json");
        boolean current = updateFile(propertiesPath, propertiesContent(result), arguments.check);
        current &= updateFile(reportPath, reportContent(result), arguments.check);
        if (!current) {
            throw new IllegalStateException("Generated block sprite files are stale.");
        }

        System.out.println("Resolved " + result.resolved.size() + " of " + result.eligibleCount() + " eligible block IDs.");
        if (!result.unresolved.isEmpty()) {
            System.out.println("Unresolved: " + String.join(", ", result.unresolved));
        }
    }

    private static Result resolveBlocks(Assets assets) throws IOException {
        Result result = new Result(assets.blockIds().size());
        for (String blockId : assets.blockIds()) {
            if (EXCLUSIONS.contains(blockId)) {
                result.excluded.add(blockId);
                continue;
            }

            String override = OVERRIDES.get(blockId);
            if (override != null && assets.textureExists(override)) {
                result.add(blockId, override, "override");
                continue;
            }

            String texture = resolveFirstTexture(assets, assets.itemModelCandidates(blockId));
            if (texture != null) {
                result.add(blockId, texture, "item_model");
                continue;
            }

            texture = resolveFirstTexture(assets, assets.blockstateModelCandidates(blockId));
            if (texture != null) {
                result.add(blockId, texture, "blockstate");
            } else {
                result.unresolved.add(blockId);
            }
        }
        return result;
    }

    private static String resolveFirstTexture(Assets assets, List<String> modelIds) throws IOException {
        for (String modelId : modelIds) {
            String texture = assets.textureForModel(modelId);
            if (texture != null && atlasFor(texture) != null) {
                return texture;
            }
        }
        return null;
    }

    private static String propertiesContent(Result result) {
        StringBuilder output = new StringBuilder()
                .append("# Generated from Minecraft ").append(MINECRAFT_VERSION).append(" client assets.\n")
                .append("# Run mvn -f tools/generator-pom.xml compile exec:java to update.\n");
        result.resolved.forEach((blockId, texture) -> output.append(blockId).append('=')
                .append(atlasFor(texture)).append('|').append(texture).append('\n'));
        return output.toString();
    }

    private static String reportContent(Result result) {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("minecraft_version", MINECRAFT_VERSION);
        report.put("total_block_ids", result.total);
        report.put("eligible_block_ids", result.eligibleCount());
        report.put("resolved_block_ids", result.resolved.size());
        double coverage = result.eligibleCount() == 0 ? 100
                : Math.round(result.resolved.size() * 10000.0 / result.eligibleCount()) / 100.0;
        report.put("coverage_percent", coverage);
        report.put("resolution_counts", new TreeMap<>(result.resolutionCounts));
        report.put("excluded", result.excluded);
        report.put("unresolved", result.unresolved);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(report) + System.lineSeparator();
    }

    private static boolean updateFile(Path path, String content, boolean check) throws IOException {
        byte[] expected = content.replace("\r\n", "\n").getBytes(StandardCharsets.UTF_8);
        if (check) {
            return Files.isRegularFile(path) && java.util.Arrays.equals(Files.readAllBytes(path), expected);
        }
        Files.createDirectories(path.getParent());
        Files.write(path, expected);
        return true;
    }

    private static String atlasFor(String textureId) {
        String path = splitId(textureId)[1];
        if (path.startsWith("block/")) return "minecraft:blocks";
        if (path.startsWith("item/")) return "minecraft:items";
        if (path.startsWith("entity/chest/")) return "minecraft:chests";
        if (path.startsWith("entity/")) return "minecraft:player_head";
        return null;
    }

    private static String normalizeId(String resourceId) {
        String[] parts = splitId(resourceId);
        return parts[0] + ':' + parts[1];
    }

    private static String[] splitId(String resourceId) {
        int separator = resourceId.indexOf(':');
        return separator < 0
                ? new String[] { "minecraft", resourceId }
                : new String[] { resourceId.substring(0, separator), resourceId.substring(separator + 1) };
    }

    private static Path defaultClientJar() {
        String appData = System.getenv("APPDATA");
        Path minecraft = appData == null
                ? Path.of(System.getProperty("user.home"), ".minecraft")
                : Path.of(appData, ".minecraft");
        return minecraft.resolve("versions").resolve(MINECRAFT_VERSION).resolve(MINECRAFT_VERSION + ".jar");
    }

    private static final class Assets implements AutoCloseable {
        private final ZipFile archive;
        private final Set<String> names = new HashSet<>();
        private final Map<String, JsonObject> jsonCache = new HashMap<>();
        private final Map<String, Map<String, String>> modelCache = new HashMap<>();
        private final List<String> blockIds;

        Assets(Path clientJar) throws IOException {
            archive = new ZipFile(clientJar.toFile());
            archive.stream().map(ZipEntry::getName).forEach(names::add);
            String prefix = "assets/minecraft/blockstates/";
            String suffix = ".json";
            blockIds = names.stream()
                    .filter(name -> name.startsWith(prefix) && name.endsWith(suffix))
                    .map(name -> name.substring(prefix.length(), name.length() - suffix.length()))
                    .sorted()
                    .toList();
        }

        List<String> blockIds() {
            return blockIds;
        }

        JsonObject readJson(String path) throws IOException {
            if (!names.contains(path)) return null;
            JsonObject cached = jsonCache.get(path);
            if (cached != null) return cached;
            try (Reader reader = new InputStreamReader(archive.getInputStream(archive.getEntry(path)), StandardCharsets.UTF_8)) {
                JsonObject parsed = JsonParser.parseReader(reader).getAsJsonObject();
                jsonCache.put(path, parsed);
                return parsed;
            }
        }

        boolean textureExists(String textureId) {
            String[] parts = splitId(textureId);
            return names.contains("assets/" + parts[0] + "/textures/" + parts[1] + ".png");
        }

        List<String> itemModelCandidates(String blockId) throws IOException {
            JsonObject item = readJson("assets/minecraft/items/" + blockId + ".json");
            List<String> candidates = new ArrayList<>();
            if (item != null && item.has("model")) {
                findModelCandidates(item.get("model"), candidates);
            }
            return candidates;
        }

        List<String> blockstateModelCandidates(String blockId) throws IOException {
            JsonObject blockstate = readJson("assets/minecraft/blockstates/" + blockId + ".json");
            List<String> candidates = new ArrayList<>();
            if (blockstate == null) return candidates;

            if (blockstate.has("variants")) {
                JsonObject variants = blockstate.getAsJsonObject("variants");
                variants.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .forEach(entry -> collectBlockstateModels(entry.getValue(), candidates));
            }
            if (blockstate.has("multipart")) {
                for (JsonElement part : blockstate.getAsJsonArray("multipart")) {
                    JsonObject object = part.getAsJsonObject();
                    if (object.has("apply")) collectBlockstateModels(object.get("apply"), candidates);
                }
            }
            return candidates;
        }

        String textureForModel(String modelId) throws IOException {
            Map<String, String> textures = resolveModel(modelId, new HashSet<>());
            for (String key : TEXTURE_KEYS) {
                String texture = resolveTexture(textures.get(key), textures, new HashSet<>());
                if (texture != null && textureExists(texture)) return normalizeId(texture);
            }
            for (String value : textures.values()) {
                String texture = resolveTexture(value, textures, new HashSet<>());
                if (texture != null && textureExists(texture)) return normalizeId(texture);
            }
            return null;
        }

        private Map<String, String> resolveModel(String modelId, Set<String> resolving) throws IOException {
            String normalized = normalizeId(modelId);
            Map<String, String> cached = modelCache.get(normalized);
            if (cached != null) return cached;
            if (!resolving.add(normalized)) return Map.of();

            String[] parts = splitId(normalized);
            JsonObject model = readJson("assets/" + parts[0] + "/models/" + parts[1] + ".json");
            if (model == null) return Map.of();

            Map<String, String> textures = new LinkedHashMap<>();
            if (model.has("parent")) {
                String parent = model.get("parent").getAsString();
                if (!parent.startsWith("builtin/")) textures.putAll(resolveModel(parent, resolving));
            }
            if (model.has("textures")) {
                model.getAsJsonObject("textures").entrySet().forEach(entry -> {
                    JsonElement value = entry.getValue();
                    if (value.isJsonPrimitive()) {
                        textures.put(entry.getKey(), value.getAsString());
                    } else if (value.isJsonObject() && value.getAsJsonObject().has("sprite")) {
                        textures.put(entry.getKey(), value.getAsJsonObject().get("sprite").getAsString());
                    }
                });
            }
            resolving.remove(normalized);
            modelCache.put(normalized, textures);
            return textures;
        }

        @Override
        public void close() throws IOException {
            archive.close();
        }
    }

    private static void findModelCandidates(JsonElement element, List<String> candidates) {
        if (element == null || !element.isJsonObject()) return;
        JsonObject node = element.getAsJsonObject();
        String type = node.has("type") ? node.get("type").getAsString() : "";
        if (type.equals("minecraft:model") && node.has("model")) {
            candidates.add(node.get("model").getAsString());
            return;
        }
        if (type.equals("minecraft:special") && node.has("base")) {
            candidates.add(node.get("base").getAsString());
            return;
        }

        for (String field : List.of("fallback", "on_false", "on_true", "model")) {
            if (node.has(field)) findModelCandidates(node.get(field), candidates);
        }
        for (String field : List.of("models", "cases", "entries")) {
            if (!node.has(field) || !node.get(field).isJsonArray()) continue;
            for (JsonElement child : node.getAsJsonArray(field)) {
                if (child.isJsonObject() && child.getAsJsonObject().has("model")) {
                    findModelCandidates(child.getAsJsonObject().get("model"), candidates);
                } else {
                    findModelCandidates(child, candidates);
                }
            }
        }
    }

    private static void collectBlockstateModels(JsonElement element, List<String> candidates) {
        if (element == null) return;
        if (element.isJsonArray()) {
            element.getAsJsonArray().forEach(child -> collectBlockstateModels(child, candidates));
        } else if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            if (object.has("model")) candidates.add(object.get("model").getAsString());
        }
    }

    private static String resolveTexture(String value, Map<String, String> textures, Set<String> resolving) {
        if (value == null || !value.startsWith("#")) return value;
        String key = value.substring(1);
        if (!resolving.add(key)) return null;
        return resolveTexture(textures.get(key), textures, resolving);
    }

    private static final class Result {
        private final int total;
        private final Map<String, String> resolved = new TreeMap<>();
        private final Map<String, Integer> resolutionCounts = new HashMap<>();
        private final List<String> excluded = new ArrayList<>();
        private final List<String> unresolved = new ArrayList<>();

        Result(int total) {
            this.total = total;
        }

        void add(String blockId, String texture, String category) {
            resolved.put(blockId, texture);
            resolutionCounts.merge(category, 1, Integer::sum);
        }

        int eligibleCount() {
            return total - excluded.size();
        }
    }

    private static final class Arguments {
        private Path repo = Path.of(".").toAbsolutePath().normalize();
        private Path clientJar;
        private boolean check;

        static Arguments parse(String[] args) {
            Arguments parsed = new Arguments();
            for (int index = 0; index < args.length; index++) {
                switch (args[index]) {
                    case "--repo" -> parsed.repo = Path.of(args[++index]).toAbsolutePath().normalize();
                    case "--client" -> parsed.clientJar = Path.of(args[++index]).toAbsolutePath().normalize();
                    case "--check" -> parsed.check = true;
                    case "--write" -> { }
                    default -> throw new IllegalArgumentException("Unknown argument: " + args[index]);
                }
            }
            return parsed;
        }
    }
}