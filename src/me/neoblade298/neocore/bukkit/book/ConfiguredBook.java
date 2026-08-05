package me.neoblade298.neocore.bukkit.book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.bukkit.entity.Player;

import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.shared.io.Section;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

public class ConfiguredBook {
	private static final int DEFAULT_LINE_WIDTH = 19;
	private static final int DEFAULT_LINES_PER_PAGE = 14;
	private static final BinaryTagHolder EMPTY_PAYLOAD = BinaryTagHolder.binaryTagHolder("{}");

	private final String id;
	private final String rawTitle;
	private final String permission;
	private final List<String> rewardCommands;
	private final List<Chapter> chapters = new ArrayList<Chapter>();
	private final int lineWidth;
	private final int linesPerPage;

	public ConfiguredBook(String id, Section section) {
		this.id = id;
		this.rawTitle = section.getString("title", id);
		this.permission = section.getString("permission", null);
		this.lineWidth = Math.max(4, section.getInt("line-width", DEFAULT_LINE_WIDTH));
		this.linesPerPage = Math.max(4, section.getInt("lines-per-page", DEFAULT_LINES_PER_PAGE));

		List<String> rewards = section.getStringList("rewards");
		this.rewardCommands = rewards == null ? List.of() : List.copyOf(rewards);

		Section chapterSection = section.getSection("chapters");
		if (chapterSection != null) {
			for (String key : chapterSection.getKeys()) {
				Section configuredChapter = chapterSection.getSection(key);
				if (configuredChapter != null) chapters.add(new Chapter(key, configuredChapter));
			}
			chapters.sort((first, second) -> Integer.compare(first.priority, second.priority));
		}
	}

	public String getId() {
		return id;
	}

	public String getPermission() {
		return permission;
	}

	public boolean canOpen(Player player) {
		return permission == null || permission.isBlank() || player.hasPermission(permission);
	}

	public List<String> getRewardCommands() {
		return Collections.unmodifiableList(rewardCommands);
	}

	public boolean hasRewards() {
		return !rewardCommands.isEmpty();
	}

	public int getChapterCount() {
		return chapters.size();
	}

	public Chapter getChapter(int index) {
		return index >= 0 && index < chapters.size() ? chapters.get(index) : null;
	}

	public int getChapterIndex(String id) {
		for (int index = 0; index < chapters.size(); index++) {
			if (chapters.get(index).id.equalsIgnoreCase(id)) return index;
		}
		return -1;
	}

	public List<String> getChapterIds() {
		return chapters.stream().map(chapter -> chapter.id).toList();
	}

	public String getReadTag(Chapter chapter) {
		return "book:" + id + ":" + chapter.id;
	}

	public Book buildTableOfContents(Predicate<String> hasReadTag) {
		List<Component> pages = new ArrayList<Component>();
		TextComponent.Builder page = Component.text();
		int usedLines = 3;
		page.append(mm(rawTitle)).append(Component.newline());
		page.append(Component.text("Table of Contents", NamedTextColor.DARK_GRAY)).append(Component.newline());
		page.append(divider()).append(Component.newline());

		for (int index = 0; index < chapters.size(); index++) {
			if (usedLines >= linesPerPage) {
				pages.add(page.build());
				page = Component.text();
				usedLines = 0;
			}
			Chapter chapter = chapters.get(index);
			Boolean read = hasRewards() ? hasReadTag.test(getReadTag(chapter)) : null;
			page.append(tocEntry(chapter, index, read)).append(Component.newline());
			usedLines++;
		}
		pages.add(page.build());
		return Book.book(mm(rawTitle), Component.empty(), pages);
	}

	public Book buildChapter(int index) {
		Chapter chapter = getChapter(index);
		if (chapter == null) return null;

		Component back = mm("<dark_gray>« </dark_gray><gold><u>Back to Contents</u></gold>")
				.clickEvent(customClick("book/" + id))
				.hoverEvent(HoverEvent.showText(Component.text("Return to the table of contents")));
		List<String> headerLines = wrap(chapter.rawName);
		int headerHeight = headerLines.size() + 1;
		int footerHeight = 2;
		List<String> content = new ArrayList<String>();
		for (String line : chapter.contentLines) content.addAll(wrap(line));

		List<Component> pages = new ArrayList<Component>();
		int contentIndex = 0;
		boolean firstPage = true;
		while (contentIndex < content.size() || firstPage) {
			int capacity = Math.max(1, linesPerPage - footerHeight - (firstPage ? headerHeight : 0));
			int end = Math.min(content.size(), contentIndex + capacity);
			List<String> chunk = content.subList(contentIndex, end);
			TextComponent.Builder page = Component.text();
			if (firstPage) {
				page.append(mm(chapter.rawName)).append(Component.newline());
				page.append(divider()).append(Component.newline());
			}
			if (!chunk.isEmpty()) page.append(mm(String.join("\n", chunk)));
			page.append(Component.newline()).append(Component.newline()).append(back);
			pages.add(page.build());
			contentIndex = end;
			firstPage = false;
			if (contentIndex >= content.size()) break;
		}
		return Book.book(mm(rawTitle), Component.empty(), pages);
	}

	private Component tocEntry(Chapter chapter, int index, Boolean read) {
		Component marker = read == null ? Component.empty()
				: read ? Component.text("✔ ", NamedTextColor.GREEN) : Component.text("» ", NamedTextColor.DARK_GRAY);
		String hover = Boolean.TRUE.equals(read) ? "Already read - click to revisit" : "Click to read this chapter";
		return Component.text().append(marker).append(mm(chapter.rawName)).build()
				.clickEvent(customClick("book/" + id + "/" + index))
				.hoverEvent(HoverEvent.showText(Component.text(hover)));
	}

	private static ClickEvent<ClickEvent.Payload.Custom> customClick(String value) {
		return ClickEvent.custom(Key.key("neocore", value), EMPTY_PAYLOAD);
	}

	private static Component divider() {
		return Component.text("━━━━━━━━━━", NamedTextColor.DARK_GRAY);
	}

	private static Component mm(String value) {
		return NeoCore.miniMessage().deserialize(value);
	}

	private List<String> wrap(String line) {
		List<String> output = new ArrayList<String>();
		if (line == null || line.isEmpty()) {
			output.add("");
			return output;
		}
		StringBuilder current = new StringBuilder();
		int currentLength = 0;
		for (String word : line.split(" ")) {
			int wordLength = visibleLength(word);
			if (currentLength > 0 && currentLength + 1 + wordLength > lineWidth) {
				output.add(current.toString());
				current.setLength(0);
				currentLength = 0;
			}
			if (currentLength > 0) {
				current.append(' ');
				currentLength++;
			}
			current.append(word);
			currentLength += wordLength;
		}
		output.add(current.toString());
		return output;
	}

	private static int visibleLength(String value) {
		return value.replaceAll("<[^>]*>", "").length();
	}

	public static class Chapter {
		private final String id;
		private final String rawName;
		private final int priority;
		private final List<String> contentLines;

		private Chapter(String id, Section section) {
			this.id = id;
			this.rawName = section.getString("name", id);
			this.priority = section.getInt("priority", 0);
			String content = "";
			if (section.isType("content", String.class)) {
				content = section.getString("content", "");
			}
			else if (section.isType("content", List.class)) {
				List<String> lines = section.getStringList("content");
				if (lines != null) content = String.join("\n", lines);
			}
			this.contentLines = List.of(content.split("\n", -1));
		}

		public String getId() {
			return id;
		}
	}
}