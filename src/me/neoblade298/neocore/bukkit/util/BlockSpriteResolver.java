package me.neoblade298.neocore.bukkit.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.bukkit.Material;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.object.ObjectContents;

final class BlockSpriteResolver {
	private static final String RESOURCE_NAME = "vanilla-block-sprites.properties";
	private static final Key PLAYER_HEAD_OBJECT = Key.key("minecraft", "player_head");
	private static final Map<String, SpriteRef> SPRITES = loadSprites();

	private BlockSpriteResolver() {
	}

	static SpriteRef resolve(Material material) {
		return SPRITES.get(material.getKey().value());
	}

	static int size() {
		return SPRITES.size();
	}

	private static Map<String, SpriteRef> loadSprites() {
		Properties properties = new Properties();
		try (InputStream input = BlockSpriteResolver.class.getResourceAsStream(RESOURCE_NAME)) {
			if (input == null) throw new IllegalStateException("Missing block sprite resource: " + RESOURCE_NAME);
			properties.load(input);
		} catch (IOException e) {
			throw new IllegalStateException("Unable to load block sprite resource: " + RESOURCE_NAME, e);
		}

		Map<String, SpriteRef> sprites = new HashMap<>();
		for (String material : properties.stringPropertyNames()) {
			String[] keys = properties.getProperty(material).split("\\|", 2);
			if (keys.length != 2) throw new IllegalStateException("Invalid block sprite mapping for " + material);
			sprites.put(material, new SpriteRef(Key.key(keys[0]), Key.key(keys[1])));
		}
		return Map.copyOf(sprites);
	}

	record SpriteRef(Key atlas, Key sprite) {
		Component component() {
			ObjectContents contents = atlas.equals(PLAYER_HEAD_OBJECT)
					? ObjectContents.playerHead().texture(sprite).hat(false).build()
					: ObjectContents.sprite(atlas, sprite);
			return Component.object(contents).color(NamedTextColor.WHITE);
		}
	}
}