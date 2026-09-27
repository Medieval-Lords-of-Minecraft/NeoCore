package me.neoblade298.neocore.bukkit.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.bungee.PluginMessageEvent;
import me.neoblade298.neocore.bukkit.util.Util;
import me.neoblade298.neocore.shared.proxy.ProxyMessage;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;

public class ProxyMessageListener implements PluginMessageListener, Listener {
	private static HashMap<UUID, UUID> tpCallbacks = new HashMap<UUID, UUID>();
	@Override
	public void onPluginMessageReceived(String channel, Player p, byte[] msg) {
		if (!channel.equals(ProxyMessage.CHANNEL)) return;
		ProxyMessage message;
		try {
			message = ProxyMessage.decode(msg);
		}
		catch (IllegalArgumentException ex) {
			NeoCore.inst().getLogger().log(java.util.logging.Level.WARNING, "Rejected malformed proxy message", ex);
			return;
		}
		ArrayList<String> fields = new ArrayList<>(message.fields());
		try {
			switch (message.type()) {
			case "neocore-tp-instant": 
				requireFields(message, 2);
				handleTeleportInstant(UUID.fromString(fields.get(0)), UUID.fromString(fields.get(1)));
				break;
			case "neocore-tp":
				requireFields(message, 2);
				tpCallbacks.put(UUID.fromString(fields.get(0)), UUID.fromString(fields.get(1)));
				break;
			case "mutablebc":
				requireFields(message, 2);
				handleMutableBroadcast(fields.get(0), fields.get(1));
				break;
			case "neocore-afk":
				requireFields(message, 2);
				handleAfkBroadcast(fields.get(0), fields.get(1).equals("T"));
				break;
			default:
				Bukkit.getPluginManager().callEvent(new PluginMessageEvent(message.type(), fields));
				break;
			}
		}
		catch (IllegalArgumentException ex) {
			NeoCore.inst().getLogger().log(java.util.logging.Level.WARNING,
					"Rejected invalid proxy message of type " + message.type(), ex);
		}
	}

	private void requireFields(ProxyMessage message, int count) {
		if (message.fields().size() != count) {
			throw new IllegalArgumentException("Proxy message " + message.type() + " expected " + count
					+ " fields but received " + message.fields().size());
		}
	}
	
	private void handleAfkBroadcast(String name, boolean isAfk) {
		Util.broadcast("<gray>* " + name + (isAfk ? " is now AFK" : " is no longer AFK"), false);
	}
	
	private void handleTeleportInstant(UUID src, UUID trg) {
		Player psrc = Bukkit.getPlayer(src);
		Player ptrg = Bukkit.getPlayer(trg);
		if (psrc != null && ptrg != null ) {
			psrc.teleport(ptrg);
		}
	}
	
	private void handleMutableBroadcast(String tagForMute, String msg) {
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			Player p = Objects.requireNonNull(onlinePlayer);
			if (!NeoCore.getNeoCoreTags().exists(tagForMute, p.getUniqueId())) {
				Util.msg(p, JSONComponentSerializer.json().deserialize(msg), false);
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
