package me.neoblade298.neocore.shared.io;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class SQLManager {
	private static final String[] TABLE_SCHEMAS = {
		"CREATE TABLE IF NOT EXISTS neocore_tags ("
			+ "uuid CHAR(36) NOT NULL, `key` VARCHAR(255) NOT NULL, tag VARCHAR(255) NOT NULL, "
			+ "expiration BIGINT NOT NULL, PRIMARY KEY (uuid, `key`, tag))"
	};
	private static volatile Map<String, String> userDbs = Collections.emptyMap();
    private static volatile Map<String, HikariDataSource> dataSources = Collections.emptyMap();
    private static volatile boolean enabled = false;

	public static void load(Section sec) {
		load(sec, Logger.getLogger(SQLManager.class.getName()));
	}
	
	public static synchronized void load(Section sec, Logger logger) {
		Objects.requireNonNull(logger, "logger");
		shutdown();
		if (sec == null) {
			logger.warning("[NeoCore] Failed to enable SQLManager as the sql config section doesn't exist");
			return;
		}
		String connectionPrefix = "jdbc:mysql://" + sec.getString("host") + ":" + sec.getString("port") + "/"; 
		String connectionSuffix = sec.getString("flags", "");
		String primaryDatabase = sec.getString("db");
		Map<String, String> newUserDbs = new HashMap<String, String>();
		Map<String, HikariDataSource> newDataSources = new HashMap<String, HikariDataSource>();

		try {
		    HikariConfig config = new HikariConfig();
		    config.setDriverClassName("com.mysql.cj.jdbc.Driver");
	        config.setJdbcUrl(connectionPrefix + primaryDatabase + connectionSuffix);
	        config.setUsername(sec.getString("username"));
	        config.setPassword(sec.getString("password"));
	        config.setMaximumPoolSize(8);
	        config.setConnectionTimeout(5000);
	        config.setLeakDetectionThreshold(30000);
	        newDataSources.put(null, new HikariDataSource(config));
			
			Section users = sec.getSection("users");
			if (users != null) {
				for (String user : users.getKeys()) {
					String db = users.getString(user);
					String dataSourceKey = primaryDatabase.equals(db) ? null : db;
					newUserDbs.put(user.toUpperCase(), dataSourceKey);
					if (dataSourceKey != null && !newDataSources.containsKey(dataSourceKey)) {
				        config.setJdbcUrl(connectionPrefix + db + connectionSuffix);
				        newDataSources.put(dataSourceKey, new HikariDataSource(config));
					}
				}
			}
			initializeTables(newDataSources);
			userDbs = Collections.unmodifiableMap(newUserDbs);
			dataSources = Collections.unmodifiableMap(newDataSources);
			enabled = true;
		} catch (SQLException | RuntimeException ex) {
			logger.log(Level.SEVERE, "[NeoCore] Failed to initialize SQL", ex);
			closeDataSources(newDataSources);
		}
	}

	private static void initializeTables(Map<String, HikariDataSource> sources) throws SQLException {
		for (HikariDataSource dataSource : sources.values()) {
			try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
				for (String tableSchema : TABLE_SCHEMAS) {
					statement.executeUpdate(tableSchema);
				}
			}
		}
	}
	
	public static String getDatabase(String user) {
		Objects.requireNonNull(user, "user");
		return userDbs.get(user.toUpperCase());
	}
	
	public static HashMap<String, HikariDataSource> getDataSources() {
		return new HashMap<String, HikariDataSource>(dataSources);
	}
	
	public static Connection getConnection(String user) throws SQLException {
		Objects.requireNonNull(user, "user");
		Map<String, HikariDataSource> sources = dataSources;
		String db = userDbs.get(user.toUpperCase());
		HikariDataSource dataSource = sources.get(db);
		if (dataSource == null) {
			throw new SQLException("SQLManager is not enabled or has no data source for user '" + user + "'");
		}
		return dataSource.getConnection();
	}
	
	public static boolean isEnabled() {
		return enabled;
	}

	public static synchronized void shutdown() {
		Map<String, HikariDataSource> oldDataSources = dataSources;
		enabled = false;
		userDbs = Collections.emptyMap();
		dataSources = Collections.emptyMap();
		closeDataSources(oldDataSources);
	}

	private static void closeDataSources(Map<String, HikariDataSource> sources) {
		for (HikariDataSource dataSource : sources.values()) {
			dataSource.close();
		}
	}
}
