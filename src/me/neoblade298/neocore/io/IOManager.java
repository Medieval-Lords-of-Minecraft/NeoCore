package me.neoblade298.neocore.io;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.TreeSet;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.neoblade298.neocore.NeoCore;

public class IOManager implements Listener {
	private static HashMap<UUID, Long> lastSave = new HashMap<UUID, Long>();
	
	// SQL
	private static HashMap<String, String> connectionStrings = new HashMap<String, String>();
	private static HashMap<String, String> pluginDbs = new HashMap<String, String>();
	private static HashMap<String, String> componentDbs = new HashMap<String, String>();
	private static String connectionPrefix, connectionSuffix;
	private static Properties properties;
	
	
	private static HashMap<String, IOComponentWrapper> components = new HashMap<String, IOComponentWrapper>();
	private static TreeSet<IOComponentWrapper> orderedComponents;
	private static HashMap<IOType, HashSet<String>> disabledIO = new HashMap<IOType, HashSet<String>>();
	private static HashMap<IOType, HashSet<UUID>> performingIO = new HashMap<IOType, HashSet<UUID>>();
	private static HashMap<IOType, HashMap<UUID, ArrayList<PostIOTask>>> postIORunnables = new HashMap<IOType, HashMap<UUID, ArrayList<PostIOTask>>>();
	private static boolean debug = false;
	
	static {
		for (IOType type : IOType.values()) {
			performingIO.put(type, new HashSet<UUID>());
			postIORunnables.put(type, new HashMap<UUID, ArrayList<PostIOTask>>());
			disabledIO.put(type, new HashSet<String>());
		}
		

		Comparator<IOComponentWrapper> comp = new Comparator<IOComponentWrapper>() {
			@Override
			public int compare(IOComponentWrapper i1, IOComponentWrapper i2) {
				if (i1.getPriority() > i2.getPriority()) {
					return -1;
				}
				else if (i1.getPriority() < i2.getPriority()) {
					return 1;
				}
				else {
					return i1.getKey().compareTo(i2.getKey());
				}
			}
		};
		orderedComponents = new TreeSet<IOComponentWrapper>(comp);
	}
	
	public IOManager(ConfigurationSection cfg) {
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
			ConfigurationSection perPlugin = alternates.getConfigurationSection("per-plugin");
			if (perPlugin != null) {
				for (String plugin : perPlugin.getKeys(false)) {
					String db = perPlugin.getString(plugin);
					pluginDbs.put(plugin.toUpperCase(), db);
					connectionStrings.putIfAbsent(db, connectionPrefix + db + connectionSuffix);
				}
			}

			ConfigurationSection perComponent = alternates.getConfigurationSection("per-component");
			if (perComponent != null) {
				for (String comp : perComponent.getKeys(false)) {
					String db = perComponent.getString(comp);
					componentDbs.put(comp.toUpperCase(), db);
					connectionStrings.putIfAbsent(db, connectionPrefix + db + connectionSuffix);
				}
			}
		}
	}

	public static IOComponentWrapper register(JavaPlugin plugin, IOComponent component, String key) {
		return register(plugin, component, key, 0);
	}
	
	public static IOComponentWrapper register(JavaPlugin plugin, IOComponent component, String key, int priority) {
		IOComponentWrapper io = new IOComponentWrapper(component, key, 0, plugin);
		components.put(key, io);
		
		// Check for component and plugin io overrides
		if (componentDbs.containsKey(key.toUpperCase())) {
			io.setDatabase(componentDbs.get(key.toUpperCase()));
		}
		else if (pluginDbs.containsKey(plugin.getName().toUpperCase())) {
			io.setDatabase(pluginDbs.get(plugin.getName().toUpperCase()));
		}
		orderedComponents.add(io);
		return io;
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		save(e.getPlayer());
	}
	
	@EventHandler
	public void onKick(PlayerKickEvent e) {
		save(e.getPlayer());
	}

	@EventHandler
	public void onPrejoin(AsyncPlayerPreLoginEvent e) {
		preload(Bukkit.getOfflinePlayer(e.getUniqueId()));
	}
	
	protected static void save(Player p) {
		HashSet<String> disabledKeys = disabledIO.get(IOType.SAVE);
		if (disabledKeys.contains("*")) {
			return;
		}
		
		UUID uuid = p.getUniqueId();
		if (lastSave.getOrDefault(uuid, 0L) + 10000 >= System.currentTimeMillis()) {
			// If saved less than 10 seconds ago, don't save again
			return;
		}
		
		long timestamp = System.currentTimeMillis();
		lastSave.put(uuid, timestamp);
		IOType type = IOType.SAVE;
		performingIO.get(type).add(uuid);
		
		new BukkitRunnable() {
			public void run() {
				try {
					// Set up statements per db
					HashMap<String, Statement> inserts = new HashMap<String, Statement>();
					HashMap<String, Statement> deletes = new HashMap<String, Statement>();
					for (Entry<String, String> e : connectionStrings.entrySet()) {
						Connection con = DriverManager.getConnection(e.getValue(), properties);
						inserts.put(e.getKey(), con.createStatement());
						deletes.put(e.getKey(), con.createStatement());
					}

					// Save account
					long timestamp = System.currentTimeMillis();
					for (IOComponentWrapper io : orderedComponents) {
						if (!disabledKeys.contains(io.getKey().toUpperCase())) {
							try {
								Statement insert = inserts.getOrDefault(io.getDatabase(), inserts.get(null));
								Statement delete = deletes.getOrDefault(io.getDatabase(), deletes.get(null));
								
								io.getComponent().savePlayer(p, insert, delete);
								int deleted = delete.executeBatch().length, inserted = insert.executeBatch().length;
								if (debug) Bukkit.getLogger().info("[NeoCore Debug] Component " + io.getKey() + " saved player " + uuid + " in " + 
										(System.currentTimeMillis() - timestamp) + "ms, +" + inserted + " -" + deleted);
							}
							catch (Exception ex) {
								Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to handle save for component " + io.getKey() + " for player " + uuid);
								ex.printStackTrace();
							}
						}
					}
				} catch (Exception ex) {
					Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to handle saving for player " + uuid);
					ex.printStackTrace();
				}
				finally {
					endIOTask(type, uuid);
					Bukkit.getLogger().info("[NeoCore] Finished saving player " + uuid + ", took " + (System.currentTimeMillis() - timestamp) + "ms");
					if (debug) Bukkit.getLogger().info("[NeoCore Debug] Finished saving at time " + System.currentTimeMillis());
				}
			}
		}.runTaskAsynchronously(NeoCore.inst());
	}
	
	public static void autosave() {
		IOType type = IOType.AUTOSAVE;
		HashSet<String> disabledKeys = disabledIO.get(type);
		if (disabledKeys.contains("*")) {
			return;
		}
		
		long timestamp = System.currentTimeMillis();
		
		ArrayList<Player> toSave = new ArrayList<Player>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			UUID uuid = p.getUniqueId();
			if (lastSave.getOrDefault(uuid, 0L) + 10000 >= System.currentTimeMillis()) {
				// If saved less than 10 seconds ago, don't save again
				continue;
			}
			lastSave.put(uuid, timestamp);
			performingIO.get(type).add(uuid);
			toSave.add(p);
		}
		
		new BukkitRunnable() {
			public void run() {
				try {
					// Set up statements per db
					HashMap<String, Statement> inserts = new HashMap<String, Statement>();
					HashMap<String, Statement> deletes = new HashMap<String, Statement>();
					for (Entry<String, String> e : connectionStrings.entrySet()) {
						Connection con = DriverManager.getConnection(e.getValue(), properties);
						inserts.put(e.getKey(), con.createStatement());
						deletes.put(e.getKey(), con.createStatement());
					}

					// Save account
					for (IOComponentWrapper io : orderedComponents) {
						if (!disabledKeys.contains(io.getKey().toUpperCase())) {
							try {
								Statement insert = inserts.getOrDefault(io.getDatabase(), inserts.get(null));
								Statement delete = deletes.getOrDefault(io.getDatabase(), deletes.get(null));
								for (Player p : toSave) {
									io.getComponent().autosavePlayer(p, insert, delete);
								}
								int deleted = delete.executeBatch().length, inserted = insert.executeBatch().length;
								if (debug) Bukkit.getLogger().info("[NeoCore Debug] Component " + io.getKey() + " autosaved players in " + 
										(System.currentTimeMillis() - timestamp) + "ms, +" + inserted + " -" + deleted);
							}
							catch (Exception ex) {
								Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to handle autosave for component " + io.getKey());
								ex.printStackTrace();
							}
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				finally {
					for (Player p : toSave) {
						endIOTask(type, p.getUniqueId());
					}
				}
			}
		}.runTaskAsynchronously(NeoCore.inst());
	}
	
	protected static void preload(OfflinePlayer p) {
		IOType type = IOType.PRELOAD;
		HashSet<String> disabledKeys = disabledIO.get(type);
		if (disabledKeys.contains("*")) {
			return;
		}
		performingIO.get(type).add(p.getUniqueId());
		
		new BukkitRunnable() {
			public void run() {
				try {
					// Set up statements per db
					HashMap<String, Statement> stmts = new HashMap<String, Statement>();
					for (Entry<String, String> e : connectionStrings.entrySet()) {
						Connection con = DriverManager.getConnection(e.getValue(), properties);
						stmts.put(e.getKey(), con.createStatement());
					}

					// Save account
					for (IOComponentWrapper io : orderedComponents) {
						if (!disabledKeys.contains(io.getKey().toUpperCase())) {
							try {
								Statement stmt = stmts.getOrDefault(io.getDatabase(), stmts.get(null));
								io.getComponent().preloadPlayer(p, stmt);
								stmt.executeBatch();
							}
							catch (Exception ex) {
								Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to handle preload for component " + io.getKey());
								ex.printStackTrace();
							}
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				finally {
					endIOTask(type, p.getUniqueId());
				}
			}
		}.runTaskAsynchronously(NeoCore.inst());
	}
	
	protected static void load(Player p) {
		IOType type = IOType.LOAD;
		HashSet<String> disabledKeys = disabledIO.get(type);
		if (disabledKeys.contains("*")) {
			return;
		}
		performingIO.get(type).add(p.getUniqueId());
		
		new BukkitRunnable() {
			int count = 0;
			public void run() {
				try {
					if (++count > 5) {
						this.cancel();
						endIOTask(type, p.getUniqueId());
						Bukkit.getLogger().warning("[NeoCore] Failed to load player " + p.getName());
						return;
					}

					// Set up statements per db
					HashMap<String, Statement> stmts = new HashMap<String, Statement>();
					for (Entry<String, String> e : connectionStrings.entrySet()) {
						Connection con = DriverManager.getConnection(e.getValue(), properties);
						stmts.put(e.getKey(), con.createStatement());
					}
					

					// Save account
					if (debug) Bukkit.getLogger().info("[NeoCore Debug] Began loading at time " + System.currentTimeMillis());
					for (IOComponentWrapper io : orderedComponents) {
						if (!disabledKeys.contains(io.getKey().toUpperCase())) {
							try {
								Statement stmt = stmts.getOrDefault(io.getDatabase(), stmts.get(null));
								io.getComponent().loadPlayer(p, stmt);
								stmt.executeBatch();
							}
							catch (Exception ex) {
								Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to handle load for component " + io.getKey());
								ex.printStackTrace();
							}
						}
					}
					Bukkit.getLogger().info("[NeoCore] Successfully loaded player " + p.getName() + " in " + count + " attempts");
					this.cancel();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				finally {
					endIOTask(type, p.getUniqueId());
				}
			}
		}.runTaskTimerAsynchronously(NeoCore.inst(), 0L, 20L);
	}
	
	public static void handleDisable() {
		HashSet<String> disabledKeys = disabledIO.get(IOType.CLEANUP);
		if (disabledKeys.contains("*")) {
			return;
		}
		
		try {
			// Set up statements per db
			long timestamp = System.currentTimeMillis();
			HashMap<String, Statement> inserts = new HashMap<String, Statement>();
			HashMap<String, Statement> deletes = new HashMap<String, Statement>();
			for (Entry<String, String> e : connectionStrings.entrySet()) {
				Connection con = DriverManager.getConnection(e.getValue(), properties);
				inserts.put(e.getKey(), con.createStatement());
				deletes.put(e.getKey(), con.createStatement());
			}
			
			// Any final cleanup
			for (IOComponentWrapper io : orderedComponents) {
				if (!disabledKeys.contains(io.getKey().toUpperCase())) {
					try {
						Bukkit.getLogger().info("[NeoCore] Cleaning up component " + io.getKey());
						Statement insert = inserts.getOrDefault(io.getDatabase(), inserts.get(null));
						Statement delete = deletes.getOrDefault(io.getDatabase(), deletes.get(null));
						io.getComponent().cleanup(insert, delete);
						int deleted = delete.executeBatch().length, inserted = insert.executeBatch().length;
						if (debug) Bukkit.getLogger().info("[NeoCore Debug] Component " + io.getKey() + " handled cleanup in " + 
								(System.currentTimeMillis() - timestamp) + "ms, +" + inserted + " -" + deleted);
					}
					catch (Exception ex) {
						Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to handle cleanup for component " + io.getKey());
						ex.printStackTrace();
					}
				}
				else {
					Bukkit.getLogger().info("[NeoCore] Skipping cleanup for component " + io.getKey());
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static Statement getStatement(String key) {
		try {
			String connectionString = components.containsKey(key) ?
					connectionStrings.get(components.get(key).getDatabase()) : connectionStrings.get(null);
			return DriverManager.getConnection(connectionString, properties).createStatement();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static Statement getStatement(IOComponentWrapper io) {
		try {
			return DriverManager.getConnection(connectionStrings.getOrDefault(io.getDatabase(), connectionStrings.get(null)), properties).createStatement();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static Statement getPluginStatement(String key) {
		String pluginDb = pluginDbs.get(key);
		try {
			return DriverManager.getConnection(connectionStrings.getOrDefault(pluginDb, connectionStrings.get(null)), properties).createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	public static void disableIO(IOType type, String key) {
		disabledIO.get(type).add(key.toUpperCase());
	}
	
	public static void disableIO(IOType type) {
		disableIO(type, "*");
	}

	public static void enableIO(IOType type, String key) {
		disabledIO.get(type).remove(key.toUpperCase());
	}

	public static void enableIO(IOType type) {
		enableIO(type, "*");
	}
	
	public static void addPostIORunnable(BukkitRunnable task, IOType type, UUID uuid, boolean async) {
		ArrayList<PostIOTask> tasks = postIORunnables.get(type).getOrDefault(uuid, new ArrayList<PostIOTask>());
		tasks.add(new PostIOTask(task, async));
		postIORunnables.get(type).putIfAbsent(uuid,	tasks);
	}
	
	public static boolean isPerformingIO(UUID uuid, IOType type) {
		return performingIO.get(type).contains(uuid);
	}
	
	private static void endIOTask(IOType type, UUID uuid) {
		performingIO.get(type).remove(uuid);
		if (postIORunnables.get(type).containsKey(uuid)) {
			for (PostIOTask task : postIORunnables.get(type).get(uuid)) {
				if (task.isAsync()) {
					task.getRunnable().runTaskAsynchronously(NeoCore.inst());
				}
				else {
					task.getRunnable().runTask(NeoCore.inst());
				}
			}
			postIORunnables.get(type).remove(uuid);
		}
	}
	
	public static TreeSet<IOComponentWrapper> getComponents() {
		return orderedComponents;
	}
	
	public static boolean toggleDebug() {
		debug = !debug;
		return debug;
	}
	
	public static HashMap<IOType, HashSet<String>> getDisabledIO() {
		return disabledIO;
	}
}
