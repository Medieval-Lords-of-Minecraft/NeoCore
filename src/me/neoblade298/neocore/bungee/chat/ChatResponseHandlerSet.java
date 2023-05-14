package me.neoblade298.neocore.bungee.chat;

import java.util.concurrent.TimeUnit;

import me.neoblade298.neocore.bungee.BungeeCore;
import me.neoblade298.neocore.bungee.listeners.ChatListener;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class ChatResponseHandlerSet {
	private ProxiedPlayer p;
	private ChatResponseHandler[] handlers;
	private int idx = 0, timeoutSeconds;
	private ScheduledTask timeout;
	public ChatResponseHandlerSet(ProxiedPlayer p, ChatResponseHandler[] handlers, int timeoutSeconds) {
		this.p = p;
		this.handlers = handlers;
		this.timeoutSeconds = timeoutSeconds;
		setupTimeout();
	}
	public ChatResponseHandler current() {
		return handlers[idx];
	}
	public boolean next() {
		if (idx + 1 >= handlers.length) {
			return false;
		}
		else {
			setupTimeout();
			idx++;
			return true;
		}
	}
	private void setupTimeout() {
		if (timeout != null) {
			timeout.cancel();
		}
		
		if (timeoutSeconds != -1) {
			BungeeCore.inst().getProxy().getScheduler().schedule(BungeeCore.inst(), () -> {
				if (p != null) {
					ChatListener.removeResponseHandlers(p);
				}
			}, timeoutSeconds, TimeUnit.SECONDS);
		}
	}
}
