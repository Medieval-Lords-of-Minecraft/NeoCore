package me.neoblade298.bungeecore.util;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import me.neoblade298.bungeecore.BungeeCore;
import me.neoblade298.neocore.util.Util;

public class BUtil {
	public static void msg(CommandSender s, String msg) {
		msg(s, msg, true);
	}
	
	public static void msg(CommandSender s, String msg, boolean hasPrefix) {
		if (hasPrefix) {
			msg = "&4[&c&lMLMC&4] &7" + msg;
		}
		s.sendMessage(new TextComponent(Util.translateColors(msg)));
	}
	
	public static void broadcast(String msg) {
		broadcast(msg, true);
	}
	
	public static void broadcast(String msg, boolean hasPrefix) {
		if (hasPrefix) {
			msg = "&4[&c&lMLMC&4] &7" + msg;
		}
		BungeeCore.inst().getProxy().broadcast(new TextComponent(Util.translateColors(msg)));
	}
}
