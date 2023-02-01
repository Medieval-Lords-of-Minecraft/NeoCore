package me.neoblade298.neocore.bungee.messaging;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import me.neoblade298.neocore.bungee.BungeeCore;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.exceptions.NeoIOException;
import me.neoblade298.neocore.shared.util.SharedUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;

public class MessagingManager {
	private static HashMap<String, BaseComponent[][]> messages = new HashMap<String, BaseComponent[][]>();
	
	public static void reload() throws NeoIOException {
		messages.clear();
		BungeeCore.loadFiles(new File(BungeeCore.inst().getDataFolder(), "messages"), (cfg, file) -> {
			for (String key : cfg.getKeys()) {
				Configuration sec = cfg.getSection(key);
				messages.put(key.toUpperCase(), parsePage(sec));
			}
		});
	}
	
	public static void sendMessage(CommandSender s, CommandSender recipient, String key) {
		sendMessage(s, recipient, key, 1);
	}
	
	public static void sendMessage(CommandSender s, CommandSender recipient, String key, int page) {
		page--;
		if (messages.containsKey(key.toUpperCase())) {
			BaseComponent[][] msgs = messages.get(key.toUpperCase());
			if (msgs.length <= page) {
				Util.msg(recipient, "&cThis doesn't have " + page + " pages!");
				return;
			}
			
			s.sendMessage(msgs[page]);
		}
		else {
			Util.msg(recipient, "&cMessage " + key + " doesn't exist!");
			BungeeCore.inst().getLogger().warning("[NeoCore] Failed to send message to " + s.getName() + ", key doesn't exist: " + key);
		}
	}
	
	public static BaseComponent[][] parsePage(Configuration cfg) {
		BaseComponent[][] list;
		if (cfg.contains("pages")) {
			Configuration pages = cfg.getSection("pages");
			Collection<String> keys = pages.getKeys();
			list = new BaseComponent[keys.size()][];
			
			int i = 0;
			for (String key : keys) {
				list[i++] = parseMessage(pages.getSection(key));
			}
		}
		else {
			list = new BaseComponent[1][];
			list[0] = parseMessage(cfg);
		}
		return list;
	}
	
	public static BaseComponent[] parseMessage(Configuration cfg) {
		boolean firstKey = true;
		ComponentBuilder builder = null;
		for (String key : cfg.getKeys()) {
			Configuration sec = cfg.getSection(key);
			if (firstKey) {
				firstKey = false;
				builder = new ComponentBuilder(SharedUtil.translateColors(sec.getString("text")));
			}
			else {
				builder.append(SharedUtil.translateColors(sec.getString("text")), FormatRetention.NONE);
			}
			
			if (sec.getString("suggest") != null) {
				builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, sec.getString("suggest")));
			}
			
			if (sec.getString("url") != null) {
				builder.event(new ClickEvent(ClickEvent.Action.OPEN_URL, sec.getString("url")));
			}
			
			if (sec.getString("run") != null) {
				builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, sec.getString("run")));
			}
			
			if (sec.getString("hover") != null) {
				builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(SharedUtil.translateColors(sec.getString("hover")))));
			}
		}
		return builder.create();
	}
}
