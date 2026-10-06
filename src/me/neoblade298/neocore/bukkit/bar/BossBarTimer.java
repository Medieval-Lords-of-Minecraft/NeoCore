package me.neoblade298.neocore.bukkit.bar;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;

public class BossBarTimer implements Listener {
	private final Plugin plugin;
	private BossBar activeBar;
	private BukkitTask timerTask;

	public BossBarTimer(Plugin plugin) {
		this.plugin = plugin;
	}

	public void start(long durationSeconds, Component message) {
		stop();

		long durationTicks = Math.multiplyExact(durationSeconds, 20L);
		activeBar = BossBar.bossBar(message, 1F, BossBar.Color.YELLOW, BossBar.Overlay.PROGRESS);
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.showBossBar(activeBar);
		}

		timerTask = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
			private long elapsedTicks;

			@Override
			public void run() {
				elapsedTicks++;
				activeBar.progress(Math.max(0F, 1F - (float) elapsedTicks / durationTicks));
				if (elapsedTicks >= durationTicks) {
					stop();
				}
			}
		}, 1L, 1L);
	}

	public void stop() {
		if (timerTask != null) {
			timerTask.cancel();
			timerTask = null;
		}
		if (activeBar != null) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.hideBossBar(activeBar);
			}
			activeBar = null;
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (activeBar != null) {
			event.getPlayer().showBossBar(activeBar);
		}
	}
}
