package me.neoblade298.neocore.bukkit.bungee;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Iterables;
import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.shared.util.SharedUtil;
import me.neoblade298.neocore.shared.proxy.ProxyMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;

public final class ProxyAPI {
	private ProxyAPI() {
	}
	private static Component prefix;
	
	static {
		prefix = Component.text("[", NamedTextColor.RED)
				.append(Component.text("MLMC", NamedTextColor.DARK_RED, TextDecoration.BOLD))
				.append(Component.text("]", NamedTextColor.RED));
	}
	
	public static void broadcast(String msg) {
		Bukkit.getLogger().info("[NeoCore Proxy] " + msg);
		dispatchProxyMessage(ProxyMessage.of("broadcast",
				JSONComponentSerializer.json().serialize(NeoCore.miniMessage().deserialize(msg))));
	}
	
	public static void mutableBroadcast(String tagForMute, Component msg) {
		mutableBroadcast(tagForMute, msg, true);
	}
	
	public static void mutableBroadcast(String tagForMute, Component msg, boolean hasPrefix) {
		msg = hasPrefix ? prefix.append(msg) : msg;
		sendPluginMessage("mutablebc", new String[] { tagForMute, JSONComponentSerializer.json().serialize(msg) });
	}
	
	public static void sendPlayer(Player p, String server) {
		if (p == null) {
			Bukkit.getLogger().warning("[NeoCore] Could not send an offline player to " + server);
			return;
		}
		dispatchProxyMessage(ProxyMessage.of("connect", p.getUniqueId().toString(), server));
	}
	
	public static void sendPluginMessage(String channel, String[] msgs) {
		sendPluginMessage(channel, msgs, false);
	}
	
	public static void sendPluginMessage(String channel, String[] msgs, boolean queue) {
		sendPluginMessage(new String[] {"ALL"}, channel, msgs, queue);
	}
	
	public static void sendPluginMessage(String[] servers, String channel, String[] msgs, boolean queue) {
		String[] fields = new String[servers.length + msgs.length + 3];
		int index = 0;
		fields[index++] = Boolean.toString(queue);
		fields[index++] = Integer.toString(servers.length);
		for (String server : servers) {
			fields[index++] = server;
		}
		fields[index++] = channel;
		for (String msg : msgs) {
			fields[index++] = msg;
		}
		dispatchProxyMessage(ProxyMessage.of("forward", fields));
	}
	
	public static void sendProxyMessage(String[] msgs) {
		if (msgs.length == 0) {
			throw new IllegalArgumentException("A proxy message requires a type");
		}
		dispatchProxyMessage(new ProxyMessage(msgs[0], java.util.Arrays.asList(msgs).subList(1, msgs.length)));
	}

	@Deprecated
	public static void sendBungeeMessage(String[] msgs) {
		sendProxyMessage(msgs);
	}

	private static void dispatchProxyMessage(ProxyMessage message) {
		Player p = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		if (p == null) {
			Bukkit.getLogger().warning("[NeoCore] Could not send proxy message without an online player: "
					+ message.type() + " " + SharedUtil.connectArgs(message.fields().toArray(String[]::new), ','));
			return;
		}
		p.sendPluginMessage(NeoCore.inst(), ProxyMessage.CHANNEL, message.encode());
	}
	
	public static void sendProxyCommand(String command) {
		dispatchProxyMessage(ProxyMessage.of("command", command));
	}

	@Deprecated
	public static void sendBungeeCommand(String command) {
		sendProxyCommand(command);
	}
}
