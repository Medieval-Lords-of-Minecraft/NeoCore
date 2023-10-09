package me.neoblade298.neocore.bukkit.chat;

import io.papermc.paper.event.player.AsyncChatEvent;

public interface ChatResponseHandler {
	// Return false to continue listening
	public boolean handle(AsyncChatEvent e);
}
