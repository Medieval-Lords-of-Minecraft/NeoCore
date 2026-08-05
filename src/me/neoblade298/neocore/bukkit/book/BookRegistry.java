package me.neoblade298.neocore.bukkit.book;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.player.PlayerTags;
import me.neoblade298.neocore.bukkit.util.Util;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class BookRegistry {
	private static final LinkedHashMap<String, ConfiguredBook> books = new LinkedHashMap<String, ConfiguredBook>();
	private static PlayerTags readTags;

	private BookRegistry() {
	}

	public static void setReadTags(PlayerTags tags) {
		readTags = tags;
	}

	public static synchronized void reload() {
		books.clear();
		NeoCore.loadFiles(new File(NeoCore.inst().getDataFolder(), "books.yml"), (config, file) -> {
			for (String key : config.getKeys()) {
				try {
					String id = key.toLowerCase();
					books.put(id, new ConfiguredBook(id, config.getSection(key)));
				}
				catch (Exception exception) {
					Bukkit.getLogger().warning("[NeoCore] Failed to load configured book " + key + " in " + file.getName());
					exception.printStackTrace();
				}
			}
		});
	}

	public static ConfiguredBook get(String id) {
		return id == null ? null : books.get(id.toLowerCase());
	}

	public static Collection<ConfiguredBook> getBooks() {
		return List.copyOf(books.values());
	}

	public static List<String> getBookIds(Player player) {
		return books.values().stream().filter(book -> book.canOpen(player)).map(ConfiguredBook::getId).toList();
	}

	public static List<String> getChapterIds(Player player, String bookId) {
		ConfiguredBook book = get(bookId);
		return book != null && book.canOpen(player) ? book.getChapterIds() : List.of();
	}

	public static boolean openTableOfContents(Player player, String bookId) {
		ConfiguredBook book = getAccessibleBook(player, bookId);
		if (book == null) return false;
		player.openBook(book.buildTableOfContents(tag -> book.hasRewards() && readTags != null
				&& readTags.exists(tag, player.getUniqueId())));
		return true;
	}

	public static boolean openChapter(Player player, String bookId, String chapterId) {
		ConfiguredBook book = getAccessibleBook(player, bookId);
		if (book == null) return false;
		int index = book.getChapterIndex(chapterId);
		if (index < 0) {
			try {
				index = Integer.parseInt(chapterId);
			}
			catch (NumberFormatException exception) {
				index = -1;
			}
		}
		return openChapter(player, book, index);
	}

	public static boolean openChapter(Player player, String bookId, int index) {
		ConfiguredBook book = getAccessibleBook(player, bookId);
		return book != null && openChapter(player, book, index);
	}

	private static boolean openChapter(Player player, ConfiguredBook book, int index) {
		ConfiguredBook.Chapter chapter = book.getChapter(index);
		if (chapter == null) {
			Util.msg(player, Component.text("That book chapter doesn't exist.", NamedTextColor.RED));
			return false;
		}

		String tag = book.getReadTag(chapter);
		if (book.hasRewards() && readTags != null && !readTags.exists(tag, player.getUniqueId())
				&& readTags.set(tag, player.getUniqueId())) {
			runRewards(player, book.getRewardCommands());
		}
		Book built = book.buildChapter(index);
		if (built == null) return false;
		player.openBook(built);
		return true;
	}

	private static ConfiguredBook getAccessibleBook(Player player, String bookId) {
		ConfiguredBook book = get(bookId);
		if (book == null) {
			Util.msg(player, Component.text("That book doesn't exist.", NamedTextColor.RED));
			return null;
		}
		if (!book.canOpen(player)) {
			Util.msg(player, Component.text("You're missing the permission: " + book.getPermission(), NamedTextColor.RED));
			return null;
		}
		return book;
	}

	private static void runRewards(Player player, List<String> commands) {
		for (String command : commands) {
			String parsed = command.replace("%player%", player.getName())
					.replace("%uuid%", player.getUniqueId().toString());
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
		}
	}
}