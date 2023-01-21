package me.neoblade298.neocore.bukkit.io;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class DefaultListener implements Listener {
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		IOManager.load(e.getPlayer());
	}
}
