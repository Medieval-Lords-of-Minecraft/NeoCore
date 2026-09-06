package me.neoblade298.neocore.bukkit.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.clip.placeholderapi.PlaceholderAPI;
import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.shared.io.Section;

public class MotdListener implements Listener {
	private static boolean enabled;
	private static String text;
	private static long delay;
	private static boolean placeholderApiEnabled;

	public static void reload(Section config) {
		enabled = config != null && config.getBoolean("enabled", false);
		text = config == null ? "" : config.getString("text", "");
		delay = config == null ? 0L : Math.max(0, config.getInt("delay", 0));
		placeholderApiEnabled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (!enabled) return;

		Bukkit.getScheduler().runTaskLater(NeoCore.inst(), () -> {
			if (!event.getPlayer().isOnline()) return;

			String parsedText = placeholderApiEnabled
					? PlaceholderAPI.setPlaceholders(event.getPlayer(), text)
					: text;
			event.getPlayer().sendMessage(NeoCore.miniMessage().deserialize(parsedText));
		}, delay);
	}
}