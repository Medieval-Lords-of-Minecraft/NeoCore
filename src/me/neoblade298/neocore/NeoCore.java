package me.neoblade298.neocore;

import java.io.File;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.neoblade298.neocore.bar.BarAPI;
import me.neoblade298.neocore.bungee.BungeeListener;
import me.neoblade298.neocore.commands.*;
import me.neoblade298.neocore.commands.builtin.*;
import me.neoblade298.neocore.commandsets.CommandSetManager;
import me.neoblade298.neocore.events.NeoCoreInitEvent;
import me.neoblade298.neocore.events.NeoPluginLoadEvent;
import me.neoblade298.neocore.exceptions.NeoIOException;
import me.neoblade298.neocore.info.InfoAPI;
import me.neoblade298.neocore.instancing.InstanceType;
import me.neoblade298.neocore.inventories.InventoryListener;
import me.neoblade298.neocore.io.DefaultListener;
import me.neoblade298.neocore.io.FileLoader;
import me.neoblade298.neocore.io.IOComponent;
import me.neoblade298.neocore.io.IOComponentWrapper;
import me.neoblade298.neocore.io.IOManager;
import me.neoblade298.neocore.io.IOType;
import me.neoblade298.neocore.io.SkillAPIListener;
import me.neoblade298.neocore.messaging.MessagingManager;
import me.neoblade298.neocore.player.*;
import me.neoblade298.neocore.scheduler.ScheduleInterval;
import me.neoblade298.neocore.scheduler.SchedulerAPI;
import me.neoblade298.neocore.teleport.TeleportAPI;
import me.neoblade298.neocore.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;

public class NeoCore extends JavaPlugin implements Listener {
	private static NeoCore inst;
	private static HashMap<String, ArrayList<Dependant>> dependants = new HashMap<String, ArrayList<Dependant>>();
	private static Economy econ;
	private static boolean debug;
	
	// Instance information
	private static InstanceType instType = InstanceType.TOWNY;
	private static String instKey = null;
	private static String instDisplay = null;
	
	public static Random gen = new Random();
	
	
	public void onEnable() {
		inst = this;
		getServer().getPluginManager().registerEvents(this, this);
		
		// Config
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "config.yml"));
		
		// Instance config
		File instancecfg = new File(this.getDataFolder(), "instance.yml");
		if (instancecfg.exists()) {
			YamlConfiguration icfg = YamlConfiguration.loadConfiguration(instancecfg);
			instKey = icfg.getString("key");
			instDisplay = Util.translateColors(icfg.getString("display"));
			instType = InstanceType.valueOf(icfg.getString("type").toUpperCase());
		}
		
		// economy
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        econ = rsp.getProvider();
        
        // core commands
        initCommands();
        
        // Bungeecord
        BungeeListener bl = new BungeeListener();
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
	    this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", bl);
        getServer().getMessenger().registerIncomingPluginChannel( this, "neocore:bungee", bl); 
        getServer().getPluginManager().registerEvents(bl, this);
        
        // playerdata
		getServer().getPluginManager().registerEvents(new IOManager(cfg), this);
		if (instType == InstanceType.TOWNY) getServer().getPluginManager().registerEvents(new DefaultListener(), this);
		else getServer().getPluginManager().registerEvents(new SkillAPIListener(), this);
        IOManager.register(this, new PlayerDataManager(), "PlayerDataManager");
        
        // CoreBar
		getServer().getPluginManager().registerEvents(new BarAPI(), this);
		
		// Inventories
		getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        
        // Teleports
        getServer().getPluginManager().registerEvents(new TeleportAPI(), this);
        
        // CommandSets
        CommandSetManager.reload();
        
        // Info
        InfoAPI.reload();
        
        // messaging
        try {
			MessagingManager.reload();
		} catch (NeoIOException e) {
			e.printStackTrace();
		}
		
		new BukkitRunnable() {
			public void run() {
				Bukkit.getPluginManager().callEvent(new NeoCoreInitEvent());
			}
		}.runTask(this);
		
		SchedulerAPI.initialize();
		
		// Autosave
		SchedulerAPI.scheduleRepeating("NeoCore-Autosave", ScheduleInterval.FIFTEEN_MINUTES, new Runnable() {
			public void run() {
				new BukkitRunnable() {
					public void run() {
						Statement insert = getDefaultStatement();
						Statement delete = getDefaultStatement();
						for (IOComponentWrapper component : IOManager.getComponents()) {
							for (Player p : Bukkit.getOnlinePlayers()) {
								try {
									component.getComponent().autosavePlayer(p, insert, delete);
								}
								catch (Exception e) {
									Bukkit.getLogger().warning("[NeoCore] Failed to autosave player " + p.getName() + " for component " + component.getKey()
											+ ", aborting autosave for remaining players for this component.");
									e.printStackTrace();
									break;
								}
							}
						}
						try {
							delete.executeBatch();
							insert.executeBatch();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}.runTaskAsynchronously(NeoCore.inst());
			}
		});
	}

	private void initCommands() {
		CommandManager mngr = new CommandManager("core", this);
		mngr.registerCommandList("");
		mngr.register(new CmdCoreDebug());
		mngr.register(new CmdCoreSchedule());
		mngr.register(new CmdCoreMessage());
		mngr.register(new CmdCoreRawMessage());
		mngr.register(new CmdCoreSendMessage());
		mngr.register(new CmdCorePlayerMessage());
		mngr.register(new CmdCoreReload());
		mngr.register(new CmdCoreCommandSet());
		mngr.register(new CmdCoreAddTag());
		mngr.register(new CmdCoreRemoveTag());
		mngr.register(new CmdCoreSetField());
		mngr.register(new CmdCoreResetField());
		mngr.register(new CmdCoreTitle());
		
		mngr = new CommandManager("bcore", this);
		mngr.registerCommandList("");
		mngr.register(new CmdBCoreSend());
		mngr.register(new CmdBCoreBroadcast());

		mngr = new CommandManager("io", "neocore.admin", ChatColor.DARK_RED, this);
		mngr.registerCommandList("");
		mngr.register(new CmdIODebug());
		mngr.register(new CmdIOEnable());
		mngr.register(new CmdIODisable());
		mngr.register(new CmdIODisabled());
		mngr.register(new CmdIOList());
		
		mngr = new CommandManager("nbt", "neocore.admin", ChatColor.DARK_RED, this);
		mngr.registerCommandList("");
		mngr.register(new CmdNBTSet());
		mngr.register(new CmdNBTGet());
		mngr.register(new CmdNBTKeys());

		mngr = new CommandManager("fix", "neocore.admin", ChatColor.DARK_RED, this);
		mngr.register(new CmdFix());
	}
	
	public static void reload() {
		try {
			MessagingManager.reload();
			CommandSetManager.reload();
			InfoAPI.reload();
		} catch (NeoIOException e) {
			e.printStackTrace();
		}
	}
	
	public void onDisable() {
		IOManager.handleDisable();
	    org.bukkit.Bukkit.getServer().getLogger().info("NeoCore Disabled");
	    this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
	    super.onDisable();
	}
	
	public static NeoCore inst() {
		return inst;
	}
	
	public static Dependant addDependant(String msg, Dependant d) {
		ArrayList<Dependant> list = dependants.getOrDefault(msg, new ArrayList<Dependant>());
		list.add(d);
		dependants.put(msg, list);
		return d;
	}
	
	@EventHandler
	public void onDependableLoad(NeoPluginLoadEvent e) {
		String msg = e.getMessage();
		if (dependants.containsKey(msg)) {
			for (Dependant dep : dependants.get(msg)) {
				dep.run();
			}
		}
	}
	
	public static InstanceType getInstanceType() {
		return instType;
	}
	
	public static String getInstanceKey() {
		return instKey;
	}
	
	public static String getInstanceDisplay() {
		return instDisplay;
	}
	
	public static IOComponentWrapper registerIOComponent(JavaPlugin plugin, IOComponent component, String key, int priority) {
		return IOManager.register(plugin, component, key, priority);
	}
	
	public static IOComponentWrapper registerIOComponent(JavaPlugin plugin, IOComponent component, String key) {
		return IOManager.register(plugin, component, key, 0);
	}
	
	public static Statement getDefaultStatement() {
		return IOManager.getDefaultStatement();
	}
	
	public static Statement getStatement(String key) {
		return IOManager.getStatement(key);
	}
	
	public static Statement getStatement(IOComponentWrapper io) {
		return IOManager.getStatement(io);
	}
	
	public static Statement getPluginStatement(String key) {
		return IOManager.getPluginStatement(key);
	}
	
	public static void loadFiles(File load, FileLoader loader) throws NeoIOException {
		if (!load.exists()) {
			Bukkit.getLogger().warning("[NeoCore] Failed to load file " + load.getPath() + ", file doesn't exist");
			return;
		}
		
		if (load.isDirectory()) {
			for (File file : load.listFiles()) {
				loadFiles(file, loader);
			}
		}
		else {
			YamlConfiguration cfg = YamlConfiguration.loadConfiguration(load);
			loader.load(cfg, load);
		}
	}
	
	public static Economy getEconomy() {
		return econ;
	}
	
	public static PlayerFields createPlayerFields(String key, Plugin plugin, boolean hidden) {
		return PlayerDataManager.createPlayerFields(key, plugin, hidden);
	}
	
	public static PlayerTags createPlayerTags(String key, Plugin plugin, boolean hidden) {
		return PlayerDataManager.createPlayerTags(key, plugin, hidden);
	}
	
	public static PlayerFields getPlayerFields(String key) {
		return PlayerDataManager.getPlayerFields(key);
	}
	
	public static PlayerTags getPlayerTags(String key) {
		return PlayerDataManager.getPlayerTags(key);
	}
	
	public static boolean isDebug() {
		return debug;
	}
	
	public static boolean toggleDebug() {
		debug = !debug;
		return debug;
	}
	
	public static boolean isSaving(Player p) {
		return !IOManager.isPerformingIO(p.getUniqueId(), IOType.SAVE) && !IOManager.isPerformingIO(p.getUniqueId(), IOType.AUTOSAVE);
	}
	
	public static boolean isLoaded(Player p) {
		return !IOManager.isPerformingIO(p.getUniqueId(), IOType.LOAD) && !IOManager.isPerformingIO(p.getUniqueId(), IOType.PRELOAD);
	}
	
	public static boolean isPerformingIO(UUID uuid, IOType type) {
		return IOManager.isPerformingIO(uuid, type);
	}
	
	public static void addPostIORunnable(BukkitRunnable task, IOType type, UUID uuid, boolean async) {
		IOManager.addPostIORunnable(task, type, uuid, async);
	}
}
