package me.neoblade298.neocore.bukkit.io;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface IOComponent {
	public void savePlayer(Player p, Connection con, List<PreparedStatement> stmts) throws Exception;
	public void preloadPlayer(OfflinePlayer p, Statement stmt);
	public void loadPlayer(Player p, Statement stmt);
	public void cleanup(Connection con, List<PreparedStatement> stmts) throws Exception;
	public default void autosavePlayer(Player p, Connection con, List<PreparedStatement> stmts) throws Exception {}
	public default void autosave(Connection con, List<PreparedStatement> stmts) throws Exception {}
}
