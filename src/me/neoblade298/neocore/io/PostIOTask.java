package me.neoblade298.neocore.io;

import org.bukkit.scheduler.BukkitRunnable;

public class PostIOTask {
	private BukkitRunnable runnable;
	private boolean isAsync;
	public BukkitRunnable getRunnable() {
		return runnable;
	}
	public boolean isAsync() {
		return isAsync;
	}
	public PostIOTask(BukkitRunnable runnable, boolean isAsync) {
		this.runnable = runnable;
		this.isAsync = isAsync;
	}
	
}
