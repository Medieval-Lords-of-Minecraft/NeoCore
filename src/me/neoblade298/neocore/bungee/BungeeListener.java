package me.neoblade298.neocore.bungee;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import me.neoblade298.neocore.NeoCore;

public class BungeeListener implements PluginMessageListener, Listener {
	private static HashMap<UUID, UUID> tpCallbacks = new HashMap<UUID, UUID>();
	@Override
	public void onPluginMessageReceived(String channel, Player p, byte[] msg) {
		if (channel.equals("neocore:bungee")) {
			readBungeeCoreMessage(msg);
			return;
		}
		if (!channel.equals("BungeeCord")) {
			return;
		}
		ByteArrayDataInput in = ByteStreams.newDataInput(msg);
		String subchannel = in.readUTF();
		if (subchannel.startsWith("UltraEconomy")) return;
		
		short len = in.readShort();
		byte[] msgbytes = new byte[len];

		in.readFully(msgbytes);
		DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
		ArrayList<String> msgs = new ArrayList<String>();
		
		// Limit messages to 10 just in case some error
		for (int i = 0; i < 10; i++) {
			try {
				msgs.add(msgin.readUTF());
			}
			catch (EOFException e) {
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Pre-read plugin message for any neocore messages, return true if the message was handled
		Bukkit.getPluginManager().callEvent(new PluginMessageEvent(subchannel, msgs));
	}
	
	private void readBungeeCoreMessage(byte[] msg) {
		ByteArrayDataInput in = ByteStreams.newDataInput(msg);
		String subchannel = in.readUTF();
		if (subchannel.equals("neocore-tp-instant")) {
			UUID src = UUID.fromString(in.readUTF());
			UUID trg = UUID.fromString(in.readUTF());
			Player psrc = Bukkit.getPlayer(src);
			Player ptrg = Bukkit.getPlayer(trg);
			if (psrc != null && ptrg != null ) {
				psrc.teleport(ptrg);
			}
		}
		else if (subchannel.equals("neocore-tp")) {
			UUID src = UUID.fromString(in.readUTF());
			UUID trg = UUID.fromString(in.readUTF());
			tpCallbacks.put(src, trg);
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		UUID uuid = e.getPlayer().getUniqueId();
		if (tpCallbacks.containsKey(uuid)) {
			Player src = Bukkit.getPlayer(uuid);
			Player trg = Bukkit.getPlayer(tpCallbacks.get(uuid));
			if (src != null && trg != null ) {
				src.teleport(trg);
			}
			tpCallbacks.remove(uuid);
		}
	}
}
