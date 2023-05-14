package me.neoblade298.neocore.bungee.listeners;

import java.util.HashMap;

import me.neoblade298.neocore.bungee.chat.ChatResponseHandler;
import me.neoblade298.neocore.bungee.chat.ChatResponseHandlerSet;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ChatListener implements Listener {
	private static HashMap<ProxiedPlayer, ChatResponseHandlerSet> respHandlers = new HashMap<ProxiedPlayer, ChatResponseHandlerSet>();
	
	@EventHandler
	public void onChat(ChatEvent e) {
		if (!(e.getSender() instanceof ProxiedPlayer)) {
			return;
		}
		
		ProxiedPlayer p = (ProxiedPlayer) e.getSender();
		if (respHandlers.containsKey(p)) {
			ChatResponseHandlerSet set = respHandlers.get(p);
			
			if (!set.current().handle(e)) return;
			
			if (!set.next()) {
				respHandlers.remove(p);
			}
		}
	}
	
	public static void addChatHandler(ProxiedPlayer p, int timeoutSeconds, ChatResponseHandler... handler) {
		respHandlers.put(p, new ChatResponseHandlerSet(p, handler, timeoutSeconds));
	}
	
	public static void removeResponseHandlers(ProxiedPlayer p) {
		respHandlers.remove(p);
	}
}
