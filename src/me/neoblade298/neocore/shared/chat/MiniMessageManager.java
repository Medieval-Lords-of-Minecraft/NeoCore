package me.neoblade298.neocore.shared.chat;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bungee.BungeeCore;
import me.neoblade298.neocore.shared.io.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class MiniMessageManager {
	private static HashMap<String, Component> messages = new HashMap<String, Component>();
	private static MiniMessage mini = MiniMessage.miniMessage();
	
	public static void reloadBukkit() {
		messages.clear();
		NeoCore.loadFiles(new File(NeoCore.inst().getDataFolder(), "messages"), (cfg, file) -> {
			for (String key : cfg.getKeys()) {
				messages.put(key.toUpperCase(), cfg.isType(key, String.class) ?
						parse(cfg.getString(key)) : parse(cfg.getStringList(key)));
			}
		});
	}
	
	public static void reloadBungee() {
		messages.clear();
		BungeeCore.loadFiles(new File(BungeeCore.folder(), "messages"), (cfg, file) -> {
			for (String key : cfg.getKeys()) {
				messages.put(key.toUpperCase(), cfg.isType(key, String.class) ?
						parse(cfg.getString(key)) : parse(cfg.getStringList(key)));
			}
		});
	}
	
	public static Component get(String key) {
		return messages.get(key);
	}
	
	public static Component parseFromYaml(Config cfg, String key) {
		if (cfg.isType(key, String.class)) {
			return parse(cfg.getString(key));
		}
		else {
			return parse(cfg.getStringList(key));
		}
	}
	
	public static Component parse(List<String> msgs) {
		Builder b = Component.text();
		for (String m : msgs) {
			b.append(parse(m));
		}
		return b.build();
	}
	
	public static Component parse(String msg) {
		return mini.deserialize(msg);
	}
}
