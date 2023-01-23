package me.neoblade298.neocore.shared.io;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import org.bukkit.configuration.ConfigurationSection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import net.md_5.bungee.config.Configuration;

public class SQLManager {
	private static HashMap<String, String> userDbs = new HashMap<String, String>(); // User -> database key -> datasource
    private static HashMap<String, HikariDataSource> dataSources = new HashMap<String, HikariDataSource>();
	
    // Bukkit
	public static void load(ConfigurationSection cfg) {
		String connectionPrefix = "jdbc:mysql://" + cfg.getString("host") + ":" + cfg.getString("port") + "/"; 
		String connectionSuffix = cfg.getString("flags");

	    HikariConfig config = new HikariConfig();
        config.setJdbcUrl(connectionPrefix + cfg.getString("db") + connectionSuffix);
        config.setUsername(cfg.getString("username"));
        config.setPassword(cfg.getString("password"));
        config.setMaximumPoolSize(5);
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
	
	public static void runSql(String user, StatementExecutor exec) {
		try {
			String db = userDbs.getOrDefault(user, null);
			Statement stmt = dataSources.get(db).getConnection().createStatement();
			exec.use(stmt);
			stmt.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void runSql(String user, int numStatements, MultiStatementExecutor exec) {
		try {
			String db = userDbs.getOrDefault(user, null);
			Connection con = dataSources.get(db).getConnection();
			Statement[] stmts = new Statement[numStatements];
			for (int i = 0; i < numStatements; i++) {
				stmts[i] = con.createStatement();
			}
			exec.use(stmts);
			for (int i = 0; i < numStatements; i++) {
				stmts[i].close();
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}
