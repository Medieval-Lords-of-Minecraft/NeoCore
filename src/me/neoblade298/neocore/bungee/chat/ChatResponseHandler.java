package me.neoblade298.neocore.bungee.chat;

import net.md_5.bungee.api.event.ChatEvent;

public interface ChatResponseHandler {
	// Return false to continue listening
	public boolean handle(ChatEvent e);
}
