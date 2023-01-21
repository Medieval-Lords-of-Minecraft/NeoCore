package me.neoblade298.neocore.bukkit.player;

import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.neoblade298.neocore.bukkit.io.IOComponent;

public class PlayerDataManager implements IOComponent {
	private static HashMap<String, PlayerFields> fields = new HashMap<String, PlayerFields>();
	private static HashMap<String, PlayerTags> tags = new HashMap<String, PlayerTags>();

	@Override
	public void savePlayer(Player p, Statement insert, Statement delete) {
		for (PlayerFields pFields : fields.values()) {
			pFields.save(insert, delete, p.getUniqueId());
		}
		for (PlayerTags pTags : tags.values()) {
			pTags.save(insert, delete, p.getUniqueId());
		}
	}

	@Override
	public void preloadPlayer(OfflinePlayer p, Statement stmt) {}

	// Load instead of preload so it has time to save from boss fights
	@Override
	public void loadPlayer(Player p, Statement stmt) {
		for (PlayerFields pFields : fields.values()) {
			pFields.load(stmt, p.getUniqueId());
		}
		for (PlayerTags pTags : tags.values()) {
			pTags.load(stmt, p.getUniqueId());
		}
	}

	@Override
	public void cleanup(Statement insert, Statement delete) {}
	
	public static PlayerFields getPlayerFields(String key) {
		return fields.get(key);
	}
	
	public static PlayerTags getPlayerTags(String key) {
		return tags.get(key);
	}
	
	public static PlayerFields createPlayerFields(String key, Plugin plugin, boolean hidden) {
		if (fields.containsKey(key)) {
			Bukkit.getLogger().log(Level.INFO, "[NeoCore] Player fields " + key + " for plugin " + plugin.getName() + " already exists. Returning existing keyed player data.");
			return fields.get(key);
		}
		Bukkit.getLogger().log(Level.INFO, "[NeoCore] Created player fields of " + key + " for plugin " + plugin.getName() + ", hidden: " + hidden + ".");
		PlayerFields newFields = new PlayerFields(key, hidden);
		fields.put(key, newFields);
		return newFields;
	}
	
	public static PlayerTags createPlayerTags(String key, Plugin plugin, boolean hidden) {
		if (tags.containsKey(key)) {
			Bukkit.getLogger().log(Level.INFO, "[NeoCore] Player tags " + key + " for plugin " + plugin.getName() + " already exists. Returning existing keyed player data.");
			return tags.get(key);
		}
		Bukkit.getLogger().log(Level.INFO, "[NeoCore] Created player tags of " + key + " for plugin " + plugin.getName() + ", hidden: " + hidden + ".");
		PlayerTags newTags = new PlayerTags(key, hidden);
		tags.put(key, newTags);
		return newTags;
	}

}
