package me.neoblade298.neocore.shared.io;

import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;

public class SQLManager implements Listener {
	private static HashMap<String, String> connectionStrings = new HashMap<String, String>();
	private static HashMap<String, String> userDbs = new HashMap<String, String>(); // User -> database key -> connection string
	private static String connectionPrefix, connectionSuffix;
	private static Properties properties;
	
	public static void load(ConfigurationSection cfg) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		ConfigurationSection sql = cfg.getConfigurationSection("sql");
		connectionPrefix = "jdbc:mysql://" + sql.getString("host") + ":" + sql.getString("port") + "/"; 
		connectionSuffix = sql.getString("flags");
		properties = new Properties();
		properties.setProperty("useSSL", "false");
		properties.setProperty("user",  sql.getString("username"));
		properties.setProperty("password", sql.getString("password"));
		connectionStrings.put(null, connectionPrefix + sql.getString("db") + connectionSuffix);
		
		ConfigurationSection alternates = cfg.getConfigurationSection("db-overrides");
		if (alternates != null) {
			ConfigurationSection perUser = alternates.getConfigurationSection("users");
			if (perUser != null) {
				for (String user : perUser.getKeys(false)) {
					String db = perUser.getString(user);
					userDbs.put(user.toUpperCase(), db);
					connectionStrings.putIfAbsent(db, connectionPrefix + db + connectionSuffix);
				}
			}
		}
	}
	
	public static String getDatabase(String user) {
		return userDbs.get(user.toUpperCase());
	}
	
	public static HashMap<String, String> getConnectionStrings() {
		return connectionStrings;
	}
	
	public static Statement getStatement(String user) {
		try {
			String connectionString = userDbs.containsKey(user) ?
					connectionStrings.get(userDbs.get(user)) : connectionStrings.get(null);
			return DriverManager.getConnection(connectionString, properties).createStatement();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static Statement getDefaultStatement() {
		try {
			return DriverManager.getConnection(connectionStrings.get(null), properties).createStatement();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static Properties getProperties() {
		return properties;
	}
}
