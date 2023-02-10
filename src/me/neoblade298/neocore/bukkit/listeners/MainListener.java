package me.neoblade298.neocore.bukkit.listeners;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.chat.ChatResponseHandler;

public class MainListener implements Listener {
	static HashMap<Player, ChatResponseHandler> chatHandlers = new HashMap<Player, ChatResponseHandler>();
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (chatHandlers.containsKey(p)) {
			if (chatHandlers.get(p).handleChat(e)) {
				chatHandlers.remove(p);
			}
		}
	}
	
	public static void addChatHandler(Player p, ChatResponseHandler handler, int timeoutSeconds) {
		chatHandlers.put(p, handler);
		if (timeoutSeconds != -1) {
			new BukkitRunnable() {
				public void run() {
					if (p != null) {
						chatHandlers.remove(p);
					}
				}
			}.runTaskLater(NeoCore.inst(), timeoutSeconds * 1000);
		}
	}
}
