package me.neoblade298.neocore.bukkit.leaderboard;

import java.net.http.WebSocket.Listener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import me.neoblade298.neocore.shared.io.SQLManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.ShadowColor;

public final class LeaderboardService<K, R> implements Listener, AutoCloseable {
	private static final String ENTITY_TAG_PREFIX = "neocore_leaderboard_";
	private static final ShadowColor TEXT_SHADOW = ShadowColor.shadowColor(0xFF000000);

	private final JavaPlugin owner;
	private Options options;
	private final QueryFactory<K, R> queryFactory;
	private final Renderer<K, R> renderer;
	private final String entityTag;
	private final Map<String, ActiveDisplay<K>> displays = new LinkedHashMap<>();
	private final Map<RefreshGroup<K>, Long> refreshGenerations = new HashMap<>();
	private final List<BukkitTask> refreshTasks = new ArrayList<>();
	private Supplier<? extends Collection<Display<K>>> displayLoader;
	private volatile boolean started;
	private volatile long lifecycleGeneration;

	public LeaderboardService(JavaPlugin owner, Options options, QueryFactory<K, R> queryFactory,
			Renderer<K, R> renderer) {
		this.owner = Objects.requireNonNull(owner, "owner");
		this.options = Objects.requireNonNull(options, "options");
		this.queryFactory = Objects.requireNonNull(queryFactory, "queryFactory");
		this.renderer = Objects.requireNonNull(renderer, "renderer");
		this.entityTag = ENTITY_TAG_PREFIX + sanitizeTag(owner.getName()) + "_" + sanitizeTag(options.serviceId());
	}

	public void start(Supplier<? extends Collection<Display<K>>> displayLoader) {
		if (started) throw new IllegalStateException("Leaderboard service is already started");
		this.displayLoader = Objects.requireNonNull(displayLoader, "displayLoader");
		started = true;
		Bukkit.getPluginManager().registerEvents(this, owner);
		reload();
	}

	public void configure(Options options) {
		Objects.requireNonNull(options, "options");
		if (!this.options.serviceId().equals(options.serviceId())) {
			throw new IllegalArgumentException("serviceId cannot be changed after construction");
		}
		this.options = options;
	}

	public void reload() {
		if (!started) throw new IllegalStateException("Leaderboard service is not started");
		lifecycleGeneration++;
		cancelRefreshTasks();
		refreshGenerations.clear();

		Collection<Display<K>> definitions = List.copyOf(displayLoader.get());
		Map<String, Location> loadedLocations = new LinkedHashMap<>();
		for (Display<K> definition : definitions) {
			Location location = definition.location();
			if (location.getWorld() == null) continue;
			location.getChunk().getEntities();
			loadedLocations.put(definition.id(), location);
		}

		removeDisplays();
		removeTaggedDisplays();
		Map<RefreshGroup<K>, List<String>> locationsByGroup = new LinkedHashMap<>();
		for (Display<K> definition : definitions) {
			Location location = loadedLocations.get(definition.id());
			if (location == null) continue;
			if (displays.containsKey(definition.id())) {
				owner.getLogger().warning("Duplicate leaderboard display id '" + definition.id() + "', skipping");
				continue;
			}

			TextDisplay display = createDisplay(location, renderer.loading(definition.key()));
			display.setRotation(location.getYaw(), location.getPitch());
			display.setPersistent(true);
			display.addScoreboardTag(entityTag);
			displays.put(definition.id(), new ActiveDisplay<>(definition, display));

			RefreshGroup<K> group = new RefreshGroup<>(definition.key(),
					definition.entryLimit() == null ? options.entryLimit() : definition.entryLimit(),
					definition.refreshTicks() == null ? options.refreshTicks() : definition.refreshTicks());
			locationsByGroup.computeIfAbsent(group, ignored -> new ArrayList<>()).add(definition.id());
		}

		for (Map.Entry<RefreshGroup<K>, List<String>> entry : locationsByGroup.entrySet()) {
			RefreshGroup<K> group = entry.getKey();
			List<String> locationIds = List.copyOf(entry.getValue());
			refreshGroup(group, locationIds);
			refreshTasks.add(Bukkit.getScheduler().runTaskTimer(owner,
					() -> refreshGroup(group, locationIds), group.refreshTicks(), group.refreshTicks()));
		}
	}

	public void refresh() {
		Map<RefreshGroup<K>, List<String>> locationsByGroup = new LinkedHashMap<>();
		for (Map.Entry<String, ActiveDisplay<K>> entry : displays.entrySet()) {
			Display<K> definition = entry.getValue().definition();
			RefreshGroup<K> group = new RefreshGroup<>(definition.key(),
					definition.entryLimit() == null ? options.entryLimit() : definition.entryLimit(),
					definition.refreshTicks() == null ? options.refreshTicks() : definition.refreshTicks());
			locationsByGroup.computeIfAbsent(group, ignored -> new ArrayList<>()).add(entry.getKey());
		}
		for (Map.Entry<RefreshGroup<K>, List<String>> entry : locationsByGroup.entrySet()) {
			refreshGroup(entry.getKey(), List.copyOf(entry.getValue()));
		}
	}

	public List<Display<K>> getDisplays() {
		return displays.values().stream().map(ActiveDisplay::definition).toList();
	}

	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) {
		reload();
	}

	@EventHandler
	public void onServerLoad(ServerLoadEvent event) {
		Bukkit.getScheduler().runTask(owner, this::reload);
	}

	@Override
	public void close() {
		if (!started) return;
		started = false;
		lifecycleGeneration++;
		cancelRefreshTasks();
		refreshGenerations.clear();
		HandlerList.unregisterAll(this);
		removeDisplays();
	}

	private void refreshGroup(RefreshGroup<K> group, List<String> locationIds) {
		if (!started || locationIds.isEmpty()) return;
		long refreshGeneration = refreshGenerations.merge(group, 1L, Long::sum);
		long refreshLifecycleGeneration = lifecycleGeneration;
		Set<UUID> excludedPlayerIds = options.excludedPlayerIds();
		Bukkit.getScheduler().runTaskAsynchronously(owner, () -> {
			List<R> rows = null;
			try {
				rows = execute(queryFactory.create(group.key(), group.entryLimit(), excludedPlayerIds));
			} catch (SQLException | RuntimeException ex) {
				owner.getLogger().log(Level.SEVERE, "Failed to refresh leaderboard '" + group.key() + "'", ex);
			}
			if (!started || refreshLifecycleGeneration != lifecycleGeneration) return;
			List<R> result = rows;
			Bukkit.getScheduler().runTask(owner, () -> applyResults(group, locationIds, refreshLifecycleGeneration,
					refreshGeneration, result));
		});
	}

	private List<R> execute(SqlRequest<R> request) throws SQLException {
		List<R> rows = new ArrayList<>();
		try (Connection connection = SQLManager.getConnection(request.connectionKey());
				PreparedStatement statement = connection.prepareStatement(request.sql())) {
			for (int i = 0; i < request.parameters().size(); i++) {
				statement.setObject(i + 1, request.parameters().get(i));
			}
			try (ResultSet result = statement.executeQuery()) {
				while (result.next()) rows.add(request.rowMapper().map(result));
			}
		}
		return List.copyOf(rows);
	}

	private void applyResults(RefreshGroup<K> group, List<String> locationIds, long refreshLifecycleGeneration,
			long refreshGeneration, List<R> rows) {
		if (!started || refreshLifecycleGeneration != lifecycleGeneration
				|| refreshGenerations.getOrDefault(group, 0L) != refreshGeneration) return;
		Component text = rows == null ? renderer.unavailable(group.key()) : renderer.render(group.key(), rows);
		for (String locationId : locationIds) {
			TextDisplay display = resolveDisplay(displays.get(locationId));
			if (display != null) display.text(withShadow(text));
		}
	}

	private TextDisplay resolveDisplay(ActiveDisplay<K> active) {
		if (active == null) return null;
		if (active.display().isValid()) return active.display();
		Location location = active.definition().location();
		if (location.getWorld() == null) return null;
		location.getChunk().getEntities();
		Entity entity = Bukkit.getEntity(active.display().getUniqueId());
		if (!(entity instanceof TextDisplay display) || !display.isValid()) return null;
		displays.put(active.definition().id(), new ActiveDisplay<>(active.definition(), display));
		return display;
	}

	private TextDisplay createDisplay(Location location, Component text) {
		TextDisplay display = location.getWorld().spawn(location, TextDisplay.class);
		display.text(withShadow(text));
		display.setDefaultBackground(false);
		display.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
		display.setShadowed(true);
		display.setBillboard(Billboard.FIXED);
		return display;
	}

	private Component withShadow(Component text) {
		return text.shadowColor(TEXT_SHADOW);
	}

	private void cancelRefreshTasks() {
		for (BukkitTask task : refreshTasks) task.cancel();
		refreshTasks.clear();
	}

	private void removeDisplays() {
		for (ActiveDisplay<K> active : displays.values()) {
			if (active.display().isValid()) active.display().remove();
		}
		displays.clear();
	}

	private void removeTaggedDisplays() {
		for (org.bukkit.World world : Bukkit.getWorlds()) {
			for (TextDisplay display : world.getEntitiesByClass(TextDisplay.class)) {
				if (display.getScoreboardTags().contains(entityTag)) display.remove();
			}
		}
	}

	private static String sanitizeTag(String value) {
		return value.toLowerCase(java.util.Locale.ROOT).replaceAll("[^a-z0-9_.-]", "_");
	}

	public record Options(String serviceId, long refreshTicks, int entryLimit, Set<UUID> excludedPlayerIds) {
		public Options {
			Objects.requireNonNull(serviceId, "serviceId");
			if (serviceId.isBlank()) throw new IllegalArgumentException("serviceId cannot be blank");
			if (refreshTicks <= 0) throw new IllegalArgumentException("refreshTicks must be positive");
			if (entryLimit <= 0) throw new IllegalArgumentException("entryLimit must be positive");
			excludedPlayerIds = Set.copyOf(excludedPlayerIds);
		}
	}

	public record Display<K>(String id, K key, Location location, Integer entryLimit, Long refreshTicks) {
		public Display {
			Objects.requireNonNull(id, "id");
			Objects.requireNonNull(key, "key");
			location = Objects.requireNonNull(location, "location").clone();
			if (id.isBlank()) throw new IllegalArgumentException("id cannot be blank");
			if (entryLimit != null && entryLimit <= 0) throw new IllegalArgumentException("entryLimit must be positive");
			if (refreshTicks != null && refreshTicks <= 0) throw new IllegalArgumentException("refreshTicks must be positive");
		}

		@Override
		public Location location() {
			return location.clone();
		}
	}

	public record SqlRequest<R>(String connectionKey, String sql, List<?> parameters, RowMapper<R> rowMapper) {
		public SqlRequest {
			Objects.requireNonNull(connectionKey, "connectionKey");
			Objects.requireNonNull(sql, "sql");
			parameters = List.copyOf(parameters);
			Objects.requireNonNull(rowMapper, "rowMapper");
		}
	}

	@FunctionalInterface
	public interface QueryFactory<K, R> {
		SqlRequest<R> create(K key, int entryLimit, Set<UUID> excludedPlayerIds) throws SQLException;
	}

	@FunctionalInterface
	public interface RowMapper<R> {
		R map(ResultSet result) throws SQLException;
	}

	public interface Renderer<K, R> {
		Component loading(K key);

		Component unavailable(K key);

		Component render(K key, List<R> rows);
	}

	private record RefreshGroup<K>(K key, int entryLimit, long refreshTicks) {
	}

	private record ActiveDisplay<K>(Display<K> definition, TextDisplay display) {
	}
}