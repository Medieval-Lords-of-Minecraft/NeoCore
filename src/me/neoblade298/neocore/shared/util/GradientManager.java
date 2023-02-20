package me.neoblade298.neocore.shared.util;

import java.util.HashMap;

import org.bukkit.configuration.ConfigurationSection;

import net.md_5.bungee.config.Configuration;

public class GradientManager {
	private static HashMap<String, Gradient> gradients = new HashMap<String, Gradient>();
	
	public static void load(ConfigurationSection cfg) {
		for (String key : cfg.getKeys(false)) {
			gradients.put(key, new Gradient(cfg.getStringList(key)));
		}
	}
	
	public static void load(Configuration cfg) {
		for (String key : cfg.getKeys()) {
			gradients.put(key, new Gradient(cfg.getStringList(key)));
		}
	}
	
	public static String applyGradient(String gradient, String text) {
		if (gradients.containsKey(gradient)) {
			return gradients.get(gradient).apply(text);
		}
		else {
			return text;
		}
	}
}
