package me.neoblade298.neocore.bukkit.listeners;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.chat.ChatResponseDispatch;
import me.neoblade298.neocore.bukkit.chat.ChatResponseHandler;

public class MainListener implements Listener {
	private static HashMap<Player, ChatResponseDispatch> chatHandlers = new HashMap<Player, ChatResponseDispatch>();
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (chatHandlers.containsKey(p)) {
			ChatResponseDispatch dispatch = chatHandlers.get(p);
			if (dispatch.getHandler().handleChat(dispatch.getId(), e)) {
				chatHandlers.remove(p);
			}
		}
	}
	
	public static void addChatHandler(Player p, String id, ChatResponseHandler handler, int timeoutSeconds) {
		chatHandlers.put(p, new ChatResponseDispatch(id, handler));
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
