package me.neoblade298.neocore.bungee.chat;

import com.velocitypowered.api.event.player.PlayerChatEvent;

public interface ChatResponseHandler {
	// Return false to continue listening
	public boolean handle(PlayerChatEvent e);
}
