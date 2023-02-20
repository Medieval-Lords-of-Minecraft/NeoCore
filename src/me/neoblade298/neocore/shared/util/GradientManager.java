package me.neoblade298.neocore.shared.util;

import java.util.Collection;
import java.util.HashMap;

import org.bukkit.configuration.ConfigurationSection;

import net.md_5.bungee.config.Configuration;

public class GradientManager {
	private static HashMap<String, Gradient> gradients = new HashMap<String, Gradient>();
	
	public static void load(ConfigurationSection cfg) {
		gradients.clear();
		for (String key : cfg.getKeys(false)) {
			gradients.put(key.toLowerCase(), new Gradient(key, cfg.getStringList(key)));
		}
	}
	
	public static void load(Configuration cfg) {
		gradients.clear();
		for (String key : cfg.getKeys()) {
			gradients.put(key.toLowerCase(), new Gradient(key, cfg.getStringList(key)));
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
	
	public static Collection<Gradient> getGradients() {
		return gradients.values();
	}
	
	public static Gradient get(String id) {
		return gradients.get(id.toLowerCase());
	}
}
