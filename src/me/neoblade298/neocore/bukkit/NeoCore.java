package me.neoblade298.neocore.bukkit;

import java.io.File;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.neoblade298.neocore.bukkit.bar.BarAPI;
import me.neoblade298.neocore.bukkit.bungee.BungeeAPI;
import me.neoblade298.neocore.bukkit.bungee.BungeeListener;
import me.neoblade298.neocore.bukkit.commands.*;
import me.neoblade298.neocore.bukkit.commands.builtin.*;
import me.neoblade298.neocore.bukkit.commandsets.CommandSetManager;
import me.neoblade298.neocore.bukkit.events.NeoCoreInitEvent;
import me.neoblade298.neocore.bukkit.info.InfoAPI;
import me.neoblade298.neocore.bukkit.inventories.InventoryListener;
import me.neoblade298.neocore.bukkit.io.DefaultListener;
import me.neoblade298.neocore.bukkit.io.FileLoader;
import me.neoblade298.neocore.bukkit.io.IOComponent;
import me.neoblade298.neocore.bukkit.io.IOComponentWrapper;
import me.neoblade298.neocore.bukkit.io.IOType;
import me.neoblade298.neocore.bukkit.io.PlayerIOManager;
import me.neoblade298.neocore.bukkit.io.SkillAPIListener;
import me.neoblade298.neocore.bukkit.listeners.EssentialsListener;
import me.neoblade298.neocore.bukkit.player.*;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;
import me.neoblade298.neocore.bukkit.scheduler.SchedulerAPI;
import me.neoblade298.neocore.bukkit.teleport.TeleportAPI;
import me.neoblade298.neocore.shared.exceptions.NeoIOException;
import me.neoblade298.neocore.shared.io.SQLManager;
import me.neoblade298.neocore.shared.messaging.MessagingManager;
import me.neoblade298.neocore.shared.util.SharedUtil;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;

public class NeoCore extends JavaPlugin implements Listener {
	private static NeoCore inst;
	private static Economy econ;
	private static boolean debug;
	
	// Instance information
	private static InstanceType instType = InstanceType.TOWNY;
	private static String instKey, instDisplay;
	
	private static String welcome;
	
	public static Random gen = new Random();
	
	
	public void onEnable() {
		inst = this;
		
		// Config
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "config.yml"));
		
		// Instance config
		File instancecfg = new File(this.getDataFolder(), "instance.yml");
		if (instancecfg.exists()) {
			YamlConfiguration icfg = YamlConfiguration.loadConfiguration(instancecfg);
			instKey = icfg.getString("key");
			instDisplay = SharedUtil.translateColors(icfg.getString("display"));
			instType = InstanceType.valueOf(icfg.getString("type").toUpperCase());
		}
		
		ConfigurationSection gen = cfg.getConfigurationSection("general");
		if (gen != null) {
			welcome = gen.getString("welcome", "&4[&c&lMLMC&4] &7Welcome &e%player% &7to MLMC!");
		}
		
		// economy
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                econ = rsp.getProvider();
            }
        }
        
        // SQL
		SQLManager.load(cfg.getConfigurationSection("sql"));
        
        // Main listener
        getServer().getPluginManager().registerEvents(this, this);
        
        // core commands
        initCommands();
        
        // Bungeecord
        BungeeListener bl = new BungeeListener();
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
	    this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", bl);
        getServer().getMessenger().registerIncomingPluginChannel( this, "neocore:bungee", bl); 
        getServer().getPluginManager().registerEvents(bl, this);
        
        // playerdata
		getServer().getPluginManager().registerEvents(new PlayerIOManager(), this);
		if (!instType.usesSkillAPI()) getServer().getPluginManager().registerEvents(new DefaultListener(), this);
		else getServer().getPluginManager().registerEvents(new SkillAPIListener(), this);
        PlayerIOManager.register(this, new PlayerDataManager(), "PlayerDataManager");
        
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
				PlayerIOManager.autosave();
			}
		});
		
		// Outside compatibilities
		if (getServer().getPluginManager().isPluginEnabled("Essentials")) {
			getServer().getPluginManager().registerEvents(new EssentialsListener(), this);
		}
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
		mngr.register(new CmdBCoreCmd());
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

		mngr = new CommandManager("rename", this);
		mngr.register(new CmdRename());
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
		PlayerIOManager.handleDisable();
	    org.bukkit.Bukkit.getServer().getLogger().info("NeoCore Disabled");
	    this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
	    super.onDisable();
	}
	
	public static NeoCore inst() {
		return inst;
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
		return PlayerIOManager.register(plugin, component, key, priority);
	}
	
	public static IOComponentWrapper registerIOComponent(JavaPlugin plugin, IOComponent component, String key) {
		return PlayerIOManager.register(plugin, component, key, 0);
	}
	
	public static void getStatement(String user) {
		SQLManager.getStatement(user);
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
		return !PlayerIOManager.isPerformingIO(p.getUniqueId(), IOType.SAVE) && !PlayerIOManager.isPerformingIO(p.getUniqueId(), IOType.AUTOSAVE);
	}
	
	public static boolean isLoaded(Player p) {
		return !PlayerIOManager.isPerformingIO(p.getUniqueId(), IOType.FULLLOAD);
	}
	
	public static boolean isPerformingIO(UUID uuid, IOType type) {
		return PlayerIOManager.isPerformingIO(uuid, type);
	}
	
	public static void addPostIORunnable(BukkitRunnable task, IOType type, UUID uuid, boolean async) {
		PlayerIOManager.addPostIORunnable(task, type, uuid, async);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (instType == InstanceType.HUB && !e.getPlayer().hasPlayedBefore()) {
			new BukkitRunnable() {
				public void run() {
					BungeeAPI.broadcast(welcome.replaceAll("%player%", e.getPlayer().getName()));
				}
			}.runTaskLaterAsynchronously(this, 60L);
		}
	}
}
