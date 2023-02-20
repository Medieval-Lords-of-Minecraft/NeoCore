package me.neoblade298.neocore.bukkit;

import java.io.File;
import java.sql.Connection;
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
import me.neoblade298.neocore.bukkit.chat.ChatResponseHandler;
import me.neoblade298.neocore.bukkit.commands.*;
import me.neoblade298.neocore.bukkit.commands.builtin.*;
import me.neoblade298.neocore.bukkit.commandsets.CommandSetManager;
import me.neoblade298.neocore.bukkit.events.NeoCoreInitEvent;
import me.neoblade298.neocore.bukkit.info.InfoAPI;
import me.neoblade298.neocore.bukkit.io.DefaultListener;
import me.neoblade298.neocore.bukkit.io.FileLoader;
import me.neoblade298.neocore.bukkit.io.IOComponent;
import me.neoblade298.neocore.bukkit.io.IOComponentWrapper;
import me.neoblade298.neocore.bukkit.io.IOType;
import me.neoblade298.neocore.bukkit.io.PlayerIOManager;
import me.neoblade298.neocore.bukkit.io.SkillAPIListener;
import me.neoblade298.neocore.bukkit.listeners.BungeeListener;
import me.neoblade298.neocore.bukkit.listeners.EssentialsListener;
import me.neoblade298.neocore.bukkit.listeners.InventoryListener;
import me.neoblade298.neocore.bukkit.listeners.MainListener;
import me.neoblade298.neocore.bukkit.messaging.MessagingManager;
import me.neoblade298.neocore.bukkit.player.*;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;
import me.neoblade298.neocore.bukkit.scheduler.SchedulerAPI;
import me.neoblade298.neocore.bukkit.teleport.TeleportAPI;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neocore.shared.exceptions.NeoIOException;
import me.neoblade298.neocore.shared.io.SQLManager;
import me.neoblade298.neocore.shared.util.GradientManager;
import me.neoblade298.neocore.shared.util.SharedUtil;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;

public class NeoCore extends JavaPlugin implements Listener {
	private static NeoCore inst;
	private static Economy econ;
	private static boolean debug;
	private static PlayerTags ptags;
	
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
        getServer().getPluginManager().registerEvents(new MainListener(), this);
        
        // core commands
        initCommands();
        
        // Bungeecord
        BungeeListener bl = new BungeeListener();
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
	    this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", bl);
        getServer().getMessenger().registerIncomingPluginChannel( this, "neocore:bungee", bl); 
        getServer().getPluginManager().registerEvents(bl, this);
        
        // io and playerdata
        if (SQLManager.isEnabled()) {
    		getServer().getPluginManager().registerEvents(new PlayerIOManager(), this);
    		if (!instType.usesSkillAPI()) getServer().getPluginManager().registerEvents(new DefaultListener(), this);
    		else getServer().getPluginManager().registerEvents(new SkillAPIListener(), this);
            PlayerIOManager.register(this, new PlayerDataManager(), "PlayerDataManager");
            
            ptags = PlayerDataManager.createPlayerTags("neocore", NeoCore.inst(), false);
        }
        
        // CoreBar
		getServer().getPluginManager().registerEvents(new BarAPI(ptags), this);
		
		// Inventories
		getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        
        // Teleports
        getServer().getPluginManager().registerEvents(new TeleportAPI(), this);
        
        // CommandSets
        CommandSetManager.reload();
        
        // Info
        InfoAPI.reload();
        
        // Gradients
        GradientManager.load(YamlConfiguration.loadConfiguration(new File("/home/MLMC/Resources/shared/NeoCore/gradients.yml")));
        
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
		SubcommandManager mngr = new SubcommandManager("core", "neocore.admin", ChatColor.DARK_RED, this);
		mngr.registerCommandList("");
		mngr.register(new CmdCoreBroadcast("bc", "Broadcasts to only the server you're on", null, SubcommandRunner.BOTH));
		mngr.register(new CmdCoreDebug("debug", "Toggles debug mode", null, SubcommandRunner.BOTH));
		mngr.register(new CmdCoreSchedule("schedule", "Lists all items in the current scheduler", null, SubcommandRunner.BOTH));
		mngr.register(new CmdCoreMessage("msg", "Sends a player a message", null, SubcommandRunner.BOTH));
		mngr.register(new CmdCoreRawMessage("rawmsg", "Sends a player a message without prefix", null, SubcommandRunner.BOTH));
		mngr.register(new CmdCoreSendMessage("sendmsg", "Plays a message", null, SubcommandRunner.BOTH));
		mngr.register(new CmdCorePlayerMessage("pmsg", "Plays a message, usable by player but hidden", "neocore.basic", SubcommandRunner.BOTH));
		mngr.register(new CmdCoreReload("reload", "Reloads the plugin safely", null, SubcommandRunner.BOTH));
		mngr.register(new CmdCoreCommandSet("commandset", "Runs a command set", null, SubcommandRunner.BOTH));
		mngr.register(new CmdCoreAddTag("addtag", "Adds a player tag", "neocore.basic", SubcommandRunner.BOTH));
		mngr.register(new CmdCoreRemoveTag("removetag", "Removes a player tag", "neocore.basic", SubcommandRunner.BOTH));
		mngr.register(new CmdCoreSetField("setfield", "Sets a player field", "neocore.basic", SubcommandRunner.BOTH));
		mngr.register(new CmdCoreResetField("resetfield", "Resets a player field", "neocore.basic", SubcommandRunner.BOTH));
		mngr.register(new CmdCoreTitle("title", "Sends a title to a player", null, SubcommandRunner.BOTH));

		mngr = new SubcommandManager("bcore", "neocore.admin", ChatColor.DARK_RED, this);
		mngr.registerCommandList("");
		mngr.register(new CmdBCoreCmd("cmd", "Sends a command to bungeecord", null, SubcommandRunner.BOTH));
		mngr.register(new CmdBCoreBroadcast("sbc", "Sends a broadcast cross-server without prefix", null, SubcommandRunner.BOTH));
		mngr.register(new CmdBCoreBroadcast("bc", "Sends a broadcast cross-server", null, SubcommandRunner.BOTH));
		mngr.register(new CmdBCoreMutableBroadcast("mbc", "Sends a mutable broadcast cross-server", null, SubcommandRunner.BOTH));
		mngr.register(new CmdBCoreSilentMutableBroadcast("smbc", "Sends a mutable broadcast cross-server without prefix", null, SubcommandRunner.BOTH));
		mngr.register(new CmdBCoreSend("send", "Sends a player to another server", null, SubcommandRunner.BOTH));

		mngr = new SubcommandManager("io", "neocore.admin", ChatColor.DARK_RED, this);
		mngr.registerCommandList("");
		mngr.register(new CmdIODebug("debug", "Toggles debug to view io benchmarks", null, SubcommandRunner.BOTH));
		mngr.register(new CmdIOEnable("enable", "Enables an IO action: save, preload, load, cleanup, autosave", null, SubcommandRunner.BOTH));
		mngr.register(new CmdIODisable("disable", "Enables an IO action: save, preload, load, cleanup, autosave", null, SubcommandRunner.BOTH));
		mngr.register(new CmdIODisabled("disabled", "Shows any disabled IO", null, SubcommandRunner.BOTH));
		mngr.register(new CmdIOList("list", "Lists IO Components by order of priority", null, SubcommandRunner.BOTH));
		
		mngr = new SubcommandManager("nbt", "neocore.admin", ChatColor.DARK_RED, this);
		mngr.registerCommandList("");
		mngr.register(new CmdNBTSet("set", "Sets NBT field of item in hand", null, SubcommandRunner.PLAYER_ONLY));
		mngr.register(new CmdNBTGet("get", "Gets NBT field of item in hand", null, SubcommandRunner.PLAYER_ONLY));
		mngr.register(new CmdNBTKeys("keys", "Shows all NBT keys of item in hand", null, SubcommandRunner.PLAYER_ONLY));

		mngr = new SubcommandManager("fix", "neocore.admin", ChatColor.DARK_RED, this);
		mngr.register(new CmdFix("", "Fixes player's item in hand", null, SubcommandRunner.BOTH));

		mngr = new SubcommandManager("rename", null, ChatColor.RED, this);
		mngr.register(new CmdRename("", "Renames player's item in hand", null, SubcommandRunner.PLAYER_ONLY));
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
	
	public static Connection getConnection(String user) {
		return SQLManager.getConnection(user);
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
	
	public static PlayerTags getNeoCoreTags() {
		return ptags;
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
	
	public static void promptChatResponse(Player p, ChatResponseHandler... handler) {
		MainListener.addChatHandler(p, 30, handler);
	}
	
	public static void promptChatResponse(Player p, int timeoutSeconds, ChatResponseHandler... handler) {
		MainListener.addChatHandler(p, timeoutSeconds, handler);
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
