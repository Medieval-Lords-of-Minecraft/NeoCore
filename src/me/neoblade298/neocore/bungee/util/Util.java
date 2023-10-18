package me.neoblade298.neocore.bungee.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.UUID;

import com.alessiodp.lastloginapi.api.LastLogin;
import com.alessiodp.lastloginapi.api.interfaces.LastLoginPlayer;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import me.neoblade298.neocore.bungee.BungeeCore;

public class Util {
	private static Component prefix;
	
	static {
		prefix = Component.text("[", NamedTextColor.RED)
				.append(Component.text("MLMC", NamedTextColor.DARK_RED, TextDecoration.BOLD))
				.append(Component.text("]", NamedTextColor.RED));
	}
	
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
	
	public static void msgGroup(Collection<CommandSource> s, Component msg, boolean hasPrefix) {
		for (CommandSource sender : s) {
			msg(sender, msg, hasPrefix);
		}
	}
	
	public static void msgGroup(Collection<CommandSource> s, Component msg) {
		msgGroup(s, msg, true);
	}
	
	public static void msgGroupRaw(Collection<CommandSource> s, Component msg) {
		msgGroup(s, msg, false);
	}
	
	public static void msgRaw(CommandSource s, Component msg) {
		msg(s, msg, false);
	}
	
	public static void msg(CommandSource s, Component msg) {
		msg(s, msg, true);
	}
	
	public static void msg(CommandSource s, Component msg, boolean hasPrefix) {
		s.sendMessage(hasPrefix ? prefix.append(msg.colorIfAbsent(NamedTextColor.GRAY)) : msg.colorIfAbsent(NamedTextColor.GRAY));
	}
	
	public static void broadcastRaw(Component msg) {
		broadcast(msg, false);
	}
	
	public static void broadcast(Component msg) {
		broadcast(msg, true);
	}
	
	public static void broadcast(Component msg, boolean hasPrefix) {
		msg = hasPrefix ? prefix.append(msg.colorIfAbsent(NamedTextColor.GRAY)) : msg.colorIfAbsent(NamedTextColor.GRAY);
		for (Player p : BungeeCore.proxy().getAllPlayers()) {
			p.sendMessage(msg);
		}
	}
	
	public static void mutableBroadcast(String tagForMute, Component msg) {
		mutableBroadcast(tagForMute, msg, true);
	}
	
	public static void mutableBroadcast(String tagForMute, Component msg, boolean hasPrefix) {
		msg = hasPrefix ? prefix.append(msg.colorIfAbsent(NamedTextColor.GRAY)) : msg.colorIfAbsent(NamedTextColor.GRAY);
		BungeeCore.sendPluginMessage(new String[] {"mutablebc", tagForMute, JSONComponentSerializer.json().serialize(msg)});
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
