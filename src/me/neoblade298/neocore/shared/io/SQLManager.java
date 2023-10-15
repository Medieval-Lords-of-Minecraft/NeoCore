package me.neoblade298.neocore.shared.io;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class SQLManager {
	private static HashMap<String, String> userDbs = new HashMap<String, String>(); // User -> database key -> datasource
    private static HashMap<String, HikariDataSource> dataSources = new HashMap<String, HikariDataSource>();
    private static boolean enabled = false;
	
    // Bukkit
	public static void load(Section sec) {
		if (sec == null) {
			Bukkit.getLogger().warning("[NeoCore] Failed to enable SQLManager as the sql config section doesn't exist");
			return;
		}
		String connectionPrefix = "jdbc:mysql://" + sec.getString("host") + ":" + sec.getString("port") + "/"; 
		String connectionSuffix = sec.getString("flags");

	    HikariConfig config = new HikariConfig();
	    config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl(connectionPrefix + sec.getString("db") + connectionSuffix);
        config.setUsername(sec.getString("username"));
        config.setPassword(sec.getString("password"));
        config.setMaximumPoolSize(8);
        config.setConnectionTimeout(5000);
        config.setLeakDetectionThreshold(30000);
        dataSources.put(null, new HikariDataSource(config));
		
		Section users = sec.getSection("users");
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
		enabled = true;
	}
	
	public static String getDatabase(String user) {
		return userDbs.get(user.toUpperCase());
	}
	
	public static HashMap<String, HikariDataSource> getDataSources() {
		return dataSources;
	}
	
	public static Connection getConnection(String user) {
		try {
			String db = userDbs.getOrDefault(user.toUpperCase(), null);
			return dataSources.get(db).getConnection();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static boolean isEnabled() {
		return enabled;
	}
}
