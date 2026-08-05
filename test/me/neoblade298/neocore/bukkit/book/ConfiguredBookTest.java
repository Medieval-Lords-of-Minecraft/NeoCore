package me.neoblade298.neocore.bukkit.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import me.neoblade298.neocore.shared.io.Config;
import me.neoblade298.neocore.shared.io.Section;

class ConfiguredBookTest {
	@Test
	void rewardsAndPermissionAreOptional() {
		ConfiguredBook book = new ConfiguredBook("guide", section(Map.of(
				"title", "Guide",
				"chapters", Map.of())));

		assertFalse(book.hasRewards());
		assertTrue(book.getRewardCommands().isEmpty());
		assertNull(book.getPermission());
	}

	@Test
	void loadsRewardsPermissionAndOrdersChaptersByPriority() {
		Map<Object, Object> chapters = new LinkedHashMap<Object, Object>();
		chapters.put("later", Map.of("name", "Later", "priority", 20, "content", "Second"));
		chapters.put("first", Map.of("name", "First", "priority", 10, "content", List.of("First", "chapter")));
		ConfiguredBook book = new ConfiguredBook("guide", section(Map.of(
				"title", "Guide",
				"permission", "example.guide",
				"rewards", List.of("give %player% diamond 1"),
				"chapters", chapters)));

		assertTrue(book.hasRewards());
		assertEquals("example.guide", book.getPermission());
		assertEquals(List.of("give %player% diamond 1"), book.getRewardCommands());
		assertEquals(List.of("first", "later"), book.getChapterIds());
	}

	@Test
	void bundledBookConfigurationLoads() {
		Config config = Config.load(new File("src/books.yml"));
		ConfiguredBook book = new ConfiguredBook("neorogue_guide", config.getSection("neorogue_guide"));

		assertTrue(book.hasRewards());
		assertEquals(10, book.getChapterCount());
		assertEquals("getting_started", book.getChapter(0).getId());
	}

	private static Section section(Map<?, ?> values) {
		Map<Object, Object> mapped = new LinkedHashMap<Object, Object>();
		mapped.putAll(values);
		return new Section("book", mapped);
	}
}