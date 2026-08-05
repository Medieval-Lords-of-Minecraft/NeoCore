package me.neoblade298.neocore.bukkit.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.ObjectComponent;
import net.kyori.adventure.text.object.PlayerHeadObjectContents;

class BlockSpriteResolverTest {
	@Test
	void resolvesRepresentativeBlockFamilies() {
		assertSprite(Material.STONE, "minecraft:blocks", "minecraft:block/stone");
		assertSprite(Material.OAK_STAIRS, "minecraft:blocks", "minecraft:block/oak_planks");
		assertSprite(Material.WHITE_BED, "minecraft:blocks", "minecraft:block/white_bed_foot_up");
		assertSprite(Material.CHEST, "minecraft:chests", "minecraft:entity/chest/normal");
		assertSprite(Material.CARVED_PUMPKIN, "minecraft:blocks", "minecraft:block/carved_pumpkin");
		assertSprite(Material.DRIED_GHAST, "minecraft:blocks", "minecraft:block/dried_ghast_hydration_0_north");
		assertSprite(Material.PLAYER_HEAD, "minecraft:player_head", "minecraft:entity/player/wide/steve");
		assertSprite(Material.PLAYER_WALL_HEAD, "minecraft:player_head", "minecraft:entity/player/wide/steve");
		assertSprite(Material.CREEPER_HEAD, "minecraft:player_head", "minecraft:entity/creeper/creeper");
		assertSprite(Material.SKELETON_SKULL, "minecraft:player_head", "minecraft:entity/skeleton/skeleton");
		assertSprite(Material.WITHER_SKELETON_SKULL, "minecraft:player_head", "minecraft:entity/skeleton/wither_skeleton");
		assertSprite(Material.ZOMBIE_HEAD, "minecraft:player_head", "minecraft:entity/zombie/zombie");
	}

	@Test
	void omitsTechnicalBlocks() {
		assertNull(BlockSpriteResolver.resolve(Material.BARRIER));
	}

	@Test
	void buildsDirectPlayerHeadObjectWithoutObscuringHatLayer() {
		Object componentValue = BlockSpriteResolver.resolve(Material.SKELETON_SKULL).component();
		assertInstanceOf(ObjectComponent.class, componentValue);
		ObjectComponent component = (ObjectComponent) componentValue;
		Object contentsValue = component.contents();
		assertInstanceOf(PlayerHeadObjectContents.class, contentsValue);
		PlayerHeadObjectContents contents = (PlayerHeadObjectContents) contentsValue;
		assertEquals(Key.key("minecraft:entity/skeleton/skeleton"), contents.texture());
		assertEquals(false, contents.hat());
	}

	@Test
	void generatedBlockIdsMatchPaperMaterials() {
		long resolvedCount = Arrays.stream(Material.values())
				.filter(material -> !material.isLegacy())
				.filter(material -> BlockSpriteResolver.resolve(material) != null)
				.count();

		assertTrue(resolvedCount >= BlockSpriteResolver.size() * 0.95,
				() -> "Only " + resolvedCount + " of " + BlockSpriteResolver.size()
						+ " generated block IDs match Paper materials");
	}

	private static void assertSprite(Material material, String atlas, String sprite) {
		BlockSpriteResolver.SpriteRef resolved = BlockSpriteResolver.resolve(material);
		assertEquals(Key.key(atlas), resolved.atlas());
		assertEquals(Key.key(sprite), resolved.sprite());
	}
}