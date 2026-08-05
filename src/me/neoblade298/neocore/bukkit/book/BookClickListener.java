package me.neoblade298.neocore.bukkit.book;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import me.neoblade298.neocore.bukkit.NeoCore;
import net.kyori.adventure.key.Key;

@SuppressWarnings("UnstableApiUsage")
public class BookClickListener implements Listener {
	@EventHandler
	public void onCustomClick(PlayerCustomClickEvent event) {
		Key identifier = event.getIdentifier();
		if (!identifier.namespace().equals("neocore") || !identifier.value().startsWith("book/")) return;
		if (!(event.getCommonConnection() instanceof PlayerGameConnection connection)) return;
		String[] parts = identifier.value().split("/");
		if (parts.length < 2 || parts.length > 3) return;

		Player player = connection.getPlayer();
		String bookId = parts[1];
		int chapter = -1;
		if (parts.length == 3) {
			try {
				chapter = Integer.parseInt(parts[2]);
			}
			catch (NumberFormatException exception) {
				return;
			}
		}
		int chapterIndex = chapter;
		Bukkit.getScheduler().runTask(NeoCore.inst(), () -> {
			if (chapterIndex < 0) BookRegistry.openTableOfContents(player, bookId);
			else BookRegistry.openChapter(player, bookId, chapterIndex);
		});
	}
}