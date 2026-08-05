package me.neoblade298.neocore.bukkit.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.ObjectComponent;
import net.kyori.adventure.text.object.SpriteObjectContents;

class UtilTest {
	@Test
	void resolvesSpecialItemSprites() {
		assertSprite(Material.SHIELD, "minecraft:shield_patterns", "minecraft:entity/shield/shield_base_nopattern");
		assertSprite(Material.CROSSBOW, "minecraft:items", "minecraft:item/crossbow_standby");
		assertSprite(Material.TIPPED_ARROW, "minecraft:items", "minecraft:item/tipped_arrow_base");
	}

	private static void assertSprite(Material material, String atlas, String sprite) {
		Object componentValue = Util.materialToSprite(material);
		assertInstanceOf(ObjectComponent.class, componentValue);
		Object contentsValue = ((ObjectComponent) componentValue).contents();
		assertInstanceOf(SpriteObjectContents.class, contentsValue);
		SpriteObjectContents contents = (SpriteObjectContents) contentsValue;
		assertEquals(Key.key(atlas), contents.atlas());
		assertEquals(Key.key(sprite), contents.sprite());
	}
}