package me.neoblade298.neocore.bukkit.listeners;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.neoblade298.neocore.bukkit.chat.ChatResponseHandler;
import me.neoblade298.neocore.bukkit.chat.ChatResponseHandlerSet;

public class MainListener implements Listener {
	private static HashMap<Player, ChatResponseHandlerSet> respHandlers = new HashMap<Player, ChatResponseHandlerSet>();
	
	@EventHandler
	public void onChat(AsyncChatEvent e) {
		Player p = e.getPlayer();
		if (respHandlers.containsKey(p)) {
			ChatResponseHandlerSet set = respHandlers.get(p);
			
			if (!set.current().handle(e)) return;
			
			if (!set.next()) {
				respHandlers.remove(p);
			}
		}
	}
	
	public static void addChatHandler(Player p, int timeoutSeconds, ChatResponseHandler... handler) {
		respHandlers.put(p, new ChatResponseHandlerSet(p, handler, timeoutSeconds));
	}
	
	public static void removeResponseHandlers(Player p) {
		respHandlers.remove(p);
	}
}
