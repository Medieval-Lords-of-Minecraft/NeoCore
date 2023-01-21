package me.neoblade298.neocore.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.neoblade298.neocore.bukkit.bungee.BungeeAPI;
import net.ess3.api.events.AfkStatusChangeEvent;

public class EssentialsListener implements Listener {
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onAfkChange(AfkStatusChangeEvent e) {
		BungeeAPI.broadcast("&7* " + e.getAffected().getName() + (e.getValue() ? " is now AFK" : " is no longer AFK"));
	}
}
