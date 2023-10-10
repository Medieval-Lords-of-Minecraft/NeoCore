package me.neoblade298.neocore.shared.chat;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.shared.exceptions.NeoIOException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class MiniMessageManager {
	private static HashMap<String, Component> messages = new HashMap<String, Component>();
	
	public static void reload() {
		messages.clear();
		NeoCore.loadFiles(new File(NeoCore.inst().getDataFolder(), "messages"), (cfg, file) -> {
			for (String key : cfg.getKeys()) {
				messages.put(key.toUpperCase(), cfg.isType(key, String.class) ?
						parse(cfg.getString(key)) : parse(cfg.getStringList(key)));
			}
		});
	}
	
	public static Component getMessage(String key) {
		return messages.get(key);
	}
	
	public static Component parse(List<String> msgs) {
		String msg = "";
		for (String m : msgs) {
			msg += m;
		}
		return parse(msg);
	}
	
	public static Component parse(String msg) {
		return MiniMessage.miniMessage().deserialize(msg);
	}
}
