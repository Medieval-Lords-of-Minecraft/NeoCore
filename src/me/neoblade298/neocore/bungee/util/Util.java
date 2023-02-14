package me.neoblade298.neocore.bungee.util;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Comparator;
import java.util.Set;
import java.util.UUID;

import com.alessiodp.lastloginapi.api.LastLogin;
import com.alessiodp.lastloginapi.api.interfaces.LastLoginPlayer;

import me.neoblade298.neocore.bungee.BungeeCore;
import me.neoblade298.neocore.shared.util.SharedUtil;

public class Util {
	private static Comparator<LastLoginPlayer> comp = new Comparator<LastLoginPlayer>() {
		@Override
		public int compare(LastLoginPlayer p1, LastLoginPlayer p2) {
			if (p1.getLastLogout() > p2.getLastLogout()) {
				return 1;
			}
			else if (p1.getLastLogout() < p2.getLastLogout()) {
				return -1;
			}
			else {
				return 0;
			}
		}
	};
	
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
	
	public static UUID getUniqueId(String name) {
		Set<? extends LastLoginPlayer> set = LastLogin.getApi().getPlayerByName(name);
		if (set.size() == 0) return null;
		return set.stream().max(comp).get().getPlayerUUID();
	}
	
	public static String getUsername(UUID uuid) {
		return LastLogin.getApi().getPlayer(uuid).getName();
	}
}
