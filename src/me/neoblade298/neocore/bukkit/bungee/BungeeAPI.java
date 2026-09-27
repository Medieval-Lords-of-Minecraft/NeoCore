package me.neoblade298.neocore.bukkit.bungee;

import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;

/**
 * @deprecated Use {@link ProxyAPI}. This compatibility facade will be removed in a future major release.
 */
@Deprecated
public final class BungeeAPI {
	private BungeeAPI() {
	}

	public static void broadcast(String message) {
		ProxyAPI.broadcast(message);
	}

	public static void mutableBroadcast(String tagForMute, Component message) {
		ProxyAPI.mutableBroadcast(tagForMute, message);
	}

	public static void mutableBroadcast(String tagForMute, Component message, boolean hasPrefix) {
		ProxyAPI.mutableBroadcast(tagForMute, message, hasPrefix);
	}

	public static void sendPlayer(Player player, String server) {
		ProxyAPI.sendPlayer(player, server);
	}

	public static void sendPluginMessage(String channel, String[] messages) {
		ProxyAPI.sendPluginMessage(channel, messages);
	}

	public static void sendPluginMessage(String channel, String[] messages, boolean queue) {
		ProxyAPI.sendPluginMessage(channel, messages, queue);
	}

	public static void sendPluginMessage(String[] servers, String channel, String[] messages, boolean queue) {
		ProxyAPI.sendPluginMessage(servers, channel, messages, queue);
	}

	public static void sendBungeeMessage(String[] messages) {
		ProxyAPI.sendBungeeMessage(messages);
	}

	public static void sendBungeeCommand(String command) {
		ProxyAPI.sendBungeeCommand(command);
	}
}
