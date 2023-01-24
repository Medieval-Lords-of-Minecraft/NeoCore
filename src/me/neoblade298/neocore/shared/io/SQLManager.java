package me.neoblade298.neocore.shared.io;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import org.bukkit.configuration.ConfigurationSection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import net.md_5.bungee.config.Configuration;

public class SQLManager {
	private static HashMap<String, String> userDbs = new HashMap<String, String>(); // User -> database key -> datasource
    private static HashMap<String, HikariDataSource> dataSources = new HashMap<String, HikariDataSource>();
    private static Queue<ConnectionDetails> latestConnections = new LinkedList<ConnectionDetails>();
	
    // Bukkit
	public static void load(ConfigurationSection cfg) {
		String connectionPrefix = "jdbc:mysql://" + cfg.getString("host") + ":" + cfg.getString("port") + "/"; 
		String connectionSuffix = cfg.getString("flags");

	    HikariConfig config = new HikariConfig();
        config.setJdbcUrl(connectionPrefix + cfg.getString("db") + connectionSuffix);
        config.setUsername(cfg.getString("username"));
        config.setPassword(cfg.getString("password"));
        config.setMaximumPoolSize(8);
        config.setConnectionTimeout(5000);
        config.setLeakDetectionThreshold(30000);
        dataSources.put(null, new HikariDataSource(config));
		
		ConfigurationSection users = cfg.getConfigurationSection("users");
		if (users != null) {
			for (String user : users.getKeys(false)) {
				String db = users.getString(user);
				userDbs.put(user.toUpperCase(), db);
				if (!dataSources.containsKey(db)) {
			        config.setJdbcUrl(connectionPrefix + db + connectionSuffix);
			        dataSources.put(db, new HikariDataSource(config));
				}
			}
		}
	}
	
	// Bungee
	public static void load(Configuration cfg) {
		String connectionPrefix = "jdbc:mysql://" + cfg.getString("host") + ":" + cfg.getString("port") + "/"; 
		String connectionSuffix = cfg.getString("flags");

	    HikariConfig config = new HikariConfig();
        config.setJdbcUrl(connectionPrefix + cfg.getString("db") + connectionSuffix);
        config.setUsername(cfg.getString("username"));
        config.setPassword(cfg.getString("password"));
        dataSources.put(null, new HikariDataSource(config));
		
		Configuration users = cfg.getSection("users");
		if (users != null) {
			for (String user : users.getKeys()) {
				String db = users.getString(user);
				userDbs.put(user.toUpperCase(), db);
				if (!dataSources.containsKey(db)) {
			        config.setJdbcUrl(connectionPrefix + db + connectionSuffix);
			        dataSources.put(db, new HikariDataSource(config));
				}
			}
		}
	}
	
	public static String getDatabase(String user) {
		return userDbs.get(user.toUpperCase());
	}
	
	public static HashMap<String, HikariDataSource> getDataSources() {
		return dataSources;
	}
	
	public static Connection getConnection(String user) {
		try {
			String db = userDbs.getOrDefault(user, null);
			Connection con = dataSources.get(db).getConnection();
			latestConnections.add(new ConnectionDetails(user, con));
			if (latestConnections.size() > 100) {
				latestConnections.poll();
			}
			return con;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static Queue<ConnectionDetails> getLatestConnections() {
		return latestConnections;
	}
}
