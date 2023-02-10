package me.neoblade298.neocore.bukkit.chat;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.listeners.MainListener;

public class ChatResponseHandlerSet {
	private Player p;
	private ChatResponseHandler[] handlers;
	private int idx = 0, timeoutSeconds;
	private BukkitTask timeout;
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
			timeout = new BukkitRunnable() {
				public void run() {
					if (p != null) {
						MainListener.removeResponseHandlers(p);
					}
				}
			}.runTaskLater(NeoCore.inst(), timeoutSeconds * 1000);
		}
	}
}
