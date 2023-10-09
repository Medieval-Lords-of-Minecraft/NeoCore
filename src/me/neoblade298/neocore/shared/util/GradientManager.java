package me.neoblade298.neocore.shared.util;

import java.util.Collection;
import java.util.HashMap;

import me.neoblade298.neocore.shared.io.Config;

public class GradientManager {
	private static HashMap<String, Gradient> gradients = new HashMap<String, Gradient>();
	
	public static void load(Config cfg) {
		gradients.clear();
		for (String key : cfg.getKeys()) {
			gradients.put(key.toLowerCase(), new Gradient(key, cfg.getStringList(key)));
		}
	}
	
	public static String applyGradient(String gradient, String text) {
		if (gradients.containsKey(gradient.toLowerCase())) {
			return gradients.get(gradient.toLowerCase()).apply(text);
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
