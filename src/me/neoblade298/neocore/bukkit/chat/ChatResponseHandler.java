package me.neoblade298.neocore.bukkit.chat;

import org.bukkit.event.player.AsyncPlayerChatEvent;

public interface ChatResponseHandler {
	// Return false to continue listening
	public boolean handleChat(AsyncPlayerChatEvent e);
}
