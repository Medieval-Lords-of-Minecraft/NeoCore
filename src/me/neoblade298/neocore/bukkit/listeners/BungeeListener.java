package me.neoblade298.neocore.bukkit.listeners;

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

import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.bungee.PluginMessageEvent;
import me.neoblade298.neocore.bukkit.util.Util;

public class BungeeListener implements PluginMessageListener, Listener {
	private static HashMap<UUID, UUID> tpCallbacks = new HashMap<UUID, UUID>();
	@Override
	public void onPluginMessageReceived(String channel, Player p, byte[] msg) {
		if (channel.equals("neocore:bungee")) {
			readBungeeCoreMessage(msg);
			return;
		}
	}
	
	private void readBungeeCoreMessage(byte[] msg) {
		ByteArrayDataInput in = ByteStreams.newDataInput(msg);
		String subchannel = in.readUTF();
		UUID src, trg;
		switch (subchannel) {
		case "neocore-tp-instant": 
			src = UUID.fromString(in.readUTF());
			trg = UUID.fromString(in.readUTF());
			Player psrc = Bukkit.getPlayer(src);
			Player ptrg = Bukkit.getPlayer(trg);
			if (psrc != null && ptrg != null ) {
				psrc.teleport(ptrg);
			}
			break;
		case "neocore-tp":
			src = UUID.fromString(in.readUTF());
			trg = UUID.fromString(in.readUTF());
			tpCallbacks.put(src, trg);
			break;
		case "mutablebc":
			handleMutableBroadcast(in.readUTF(), in.readUTF());
			break;
		default:
			ArrayList<String> msgs = new ArrayList<String>();
			try {
				for (int i = 0; i < 10; i++) {
					msgs.add(in.readUTF());
				}
			}
			// Read until EOF Exception
			catch (Exception e) {}
			Bukkit.getPluginManager().callEvent(new PluginMessageEvent(subchannel, msgs));
			break;
		}
	}
	
	private void handleMutableBroadcast(String msg, String tagForMute) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!NeoCore.getNeoCoreTags().exists(tagForMute, p.getUniqueId())) {
				Util.msg(p, msg);
			}
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		UUID uuid = e.getPlayer().getUniqueId();
		
		new BukkitRunnable() {
			public void run() {
				if (tpCallbacks.containsKey(uuid)) {
					Player src = Bukkit.getPlayer(uuid);
					Player trg = Bukkit.getPlayer(tpCallbacks.get(uuid));
					if (src != null && trg != null ) {
						src.teleport(trg);
					}
					tpCallbacks.remove(uuid);
				}
			}
		}.runTaskLater(NeoCore.inst(), 20L);
	}
}
