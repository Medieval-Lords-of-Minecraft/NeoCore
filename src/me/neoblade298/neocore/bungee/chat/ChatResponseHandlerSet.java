package me.neoblade298.neocore.bungee.chat;

import java.util.concurrent.TimeUnit;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scheduler.ScheduledTask;

import me.neoblade298.neocore.bungee.BungeeCore;
import me.neoblade298.neocore.bungee.listeners.ChatListener;

public class ChatResponseHandlerSet {
	private Player p;
	private ChatResponseHandler[] handlers;
	private int idx = 0, timeoutSeconds;
	private ScheduledTask timeout;
	public ChatResponseHandlerSet(Player p, ChatResponseHandler[] handlers, int timeoutSeconds) {
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
			timeout = BungeeCore.proxy().getScheduler().buildTask(BungeeCore.inst(), () -> {
				if (p != null) {
					ChatListener.removeResponseHandlers(p);
				}
			}).delay(timeoutSeconds, TimeUnit.SECONDS).schedule();
		}
	}
}
