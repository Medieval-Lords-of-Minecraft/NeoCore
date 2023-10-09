package me.neoblade298.neocore.bungee.listeners;

import java.util.HashMap;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;

import me.neoblade298.neocore.bungee.chat.ChatResponseHandler;
import me.neoblade298.neocore.bungee.chat.ChatResponseHandlerSet;

public class ChatListener {
	private static HashMap<Player, ChatResponseHandlerSet> respHandlers = new HashMap<Player, ChatResponseHandlerSet>();
	
	@Subscribe
	public void onChat(PlayerChatEvent e) {
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
