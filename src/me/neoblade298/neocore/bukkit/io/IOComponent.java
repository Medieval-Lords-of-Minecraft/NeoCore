package me.neoblade298.neocore.bukkit.io;

import java.sql.Statement;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface IOComponent {
	public void savePlayer(Player p, Statement insert, Statement delete);
	public void preloadPlayer(OfflinePlayer p, Statement stmt);
	public void loadPlayer(Player p, Statement stmt);
	public void cleanup(Statement insert, Statement delete);
	public default void autosavePlayer(Player p, Statement insert, Statement delete) {}
	public default void autosave(Statement insert, Statement delete) {}
}
