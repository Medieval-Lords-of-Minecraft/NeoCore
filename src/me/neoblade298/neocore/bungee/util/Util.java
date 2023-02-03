package me.neoblade298.neocore.bungee.util;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import me.neoblade298.neocore.bungee.BungeeCore;
import me.neoblade298.neocore.shared.util.SharedUtil;

public class Util {
	public static void msg(CommandSender s, String msg) {
		msg(s, msg, true);
	}
	
	public static void msg(CommandSender s, String msg, boolean hasPrefix) {
		if (hasPrefix) {
			msg = "&4[&c&lMLMC&4] &7" + msg;
		}
		s.sendMessage(new TextComponent(SharedUtil.translateColors(msg)));
	}
	
	public static void broadcast(String msg) {
		broadcast(msg, true);
	}
	
	public static void broadcast(String msg, boolean hasPrefix) {
		if (hasPrefix) {
			msg = "&4[&c&lMLMC&4] &7" + msg;
		}
		BungeeCore.inst().getProxy().broadcast(new TextComponent(SharedUtil.translateColors(msg)));
	}
	
	public static void mutableBroadcast(String tagForMute, String msg) {
		mutableBroadcast(tagForMute, msg, true);
	}
	
	public static void mutableBroadcast(String tagForMute, String msg, boolean hasPrefix) {
		if (hasPrefix) {
			msg = SharedUtil.translateColors("&4[&c&lMLMC&4] &7" + msg);
		}
		BungeeCore.sendPluginMessage(new String[] {"mutablebc", tagForMute,  msg});
	}
}
