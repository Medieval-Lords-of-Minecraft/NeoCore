package me.neoblade298.neocore.bukkit.player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.neoblade298.neocore.bukkit.io.IOComponent;

public class PlayerDataManager implements IOComponent {
	private static final Map<String, PlayerTags> tags = Map.of(
			"neocore", new PlayerTags("neocore", false),
			"books", new PlayerTags("books", true));

	@Override
	public void savePlayer(Player p, Connection con, List<PreparedStatement> stmts) throws Exception {
		for (PlayerTags pTags : tags.values()) {
			pTags.save(con, stmts, p.getUniqueId());
		}
	}

	@Override
	public void preloadPlayer(OfflinePlayer p, Statement stmt) {}

	// Load instead of preload so it has time to save from boss fights
	@Override
	public void loadPlayer(Player p, Statement stmt) {
		for (PlayerTags pTags : tags.values()) {
			pTags.load(stmt, p.getUniqueId());
		}
	}

	// In case neocore unloads before player kick
	@Override
	public void cleanup(Connection con, List<PreparedStatement> stmts) throws Exception {
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			Player p = Objects.requireNonNull(onlinePlayer);
			for (PlayerTags pTags : tags.values()) {
				pTags.save(con, stmts, p.getUniqueId());
			}
		}
	}

	public static PlayerTags getPlayerTags(String key) {
		return tags.get(key);
	}
}
