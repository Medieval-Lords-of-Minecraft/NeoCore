package me.neoblade298.neocore.bukkit.bungee;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.shared.util.SharedUtil;

public class BungeeAPI {
	public static void broadcast(String msg) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Message");
		out.writeUTF("ALL");
		out.writeUTF(SharedUtil.translateColors(msg));

		Player p = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		if (p == null) {
			Bukkit.getLogger().warning("[NeoCore] Could not send message due to no online players: " + msg);
			return;
		}
		Bukkit.getLogger().info("[NeoCore BC] " + msg);
		p.sendPluginMessage(NeoCore.inst(), "BungeeCord", out.toByteArray());
	}
	
	public static void mutableBroadcast(String msg, String tagForMute) {
		sendPluginMessage("mutablebc", new String[] { "mutablebc", msg, tagForMute });
	}
	
	public static void sendPlayer(Player p, String server) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(server);

		p.sendPluginMessage(NeoCore.inst(), "BungeeCord", out.toByteArray());
	}
	
	public static void sendPluginMessage(String channel, String[] msgs) {
		sendPluginMessage(channel, msgs, false);
	}
	
	public static void sendPluginMessage(String channel, String[] msgs, boolean queue) {
		sendPluginMessage(new String[] {"ALL"}, channel, msgs, queue);
	}
	
	public static void sendPluginMessage(String[] servers, String channel, String[] msgs, boolean queue) {
		int count = 0;
		String[] out = new String[servers.length + msgs.length + 5];
		out[count++] = queue ? "fwdq" : "fwd"; // Protocol
		out[count++] = NeoCore.getInstanceKey(); // From server
		for (String server : servers) { // To servers (can also use ALL, which will send to all except source)
			out[count++] = server;
		}
		out[count++] = "EOP"; // To servers delimiter (end of parameters)
		out[count++] = channel; // Channel
		for (String msg : msgs) {
			out[count++] = msg;
		}
		out[count++] = "EOP"; // Messages delimiter (end of parameters)
		sendBungeeMessage(out);
	}
	
	public static void sendBungeeMessage(String[] msgs) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("NeoCore");
		for (String msg : msgs) {
			out.writeUTF(msg);
		}
		Player p = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		
		if (p == null) {
			Bukkit.getLogger().warning("[NeoCore] Could not send bungee message due to no online players: ");
			Bukkit.getLogger().warning(SharedUtil.connectArgs(msgs, ','));
			return;
		}
		p.sendPluginMessage(NeoCore.inst(), "BungeeCord", out.toByteArray());
	}
	
	public static void sendBungeeCommand(String command) {
		sendBungeeMessage(new String[] {"cmd", command});
	}
}
