package me.neoblade298.neocore.bungee.util;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
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
}