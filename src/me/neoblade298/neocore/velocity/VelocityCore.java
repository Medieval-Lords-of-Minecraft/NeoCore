package me.neoblade298.neocore.velocity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import me.neoblade298.neocore.velocity.commands.builtin.CmdBroadcast;
import me.neoblade298.neocore.velocity.commands.builtin.CmdHub;
import me.neoblade298.neocore.velocity.commands.builtin.CmdKickAll;
import me.neoblade298.neocore.velocity.commands.builtin.CmdMotd;
import me.neoblade298.neocore.velocity.commands.builtin.CmdMutableBroadcast;
import me.neoblade298.neocore.velocity.commands.builtin.CmdSend;
import me.neoblade298.neocore.velocity.commands.builtin.CmdSendAll;
import me.neoblade298.neocore.velocity.commands.builtin.CmdSilentBroadcast;
import me.neoblade298.neocore.velocity.commands.builtin.CmdSilentMutableBroadcast;
import me.neoblade298.neocore.velocity.commands.builtin.CmdTp;
import me.neoblade298.neocore.velocity.commands.builtin.CmdTphere;
import me.neoblade298.neocore.velocity.commands.builtin.CmdUptime;
import me.neoblade298.neocore.velocity.io.FileLoader;
import me.neoblade298.neocore.velocity.listeners.VelocityListener;
import me.neoblade298.neocore.velocity.util.Util;
import me.neoblade298.neocore.shared.io.Config;
import me.neoblade298.neocore.shared.io.SQLManager;
import me.neoblade298.neocore.shared.proxy.ProxyMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

@Plugin(id = "neocore", name = "NeoCore", version = "0.1.0-SNAPSHOT",
        url = "https://ml-mc.com", description = "Neo's core plugin for his suite of plugins", authors = {"Ascheladd"})
public class VelocityCore {
	public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from(ProxyMessage.CHANNEL);
	private static final int MAX_QUEUED_MESSAGES_PER_SERVER = 256;
	private static ProxyServer proxy;
	private static Logger logger;
	private static VelocityCore inst;
	private static List<String> announceList;
	private static Config announceCfg;
	private static Component announcements;
	private static String motd;
	private static long motdDelaySeconds;
	private static String hubServer;
	private static File folder;
	private static MiniMessage mini;
	private static final Map<String, Queue<byte[]>> queuedMessages = new ConcurrentHashMap<>();
	
	private static Component joinPrefix, leavePrefix;
	
	@Inject
	public VelocityCore(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        inst = this;
		VelocityCore.proxy = server;
		VelocityCore.logger = logger;
		folder = dataDirectory.toFile();
	}
	
	@Subscribe
    public void onProxyInitialization(ProxyInitializeEvent e) {
		mini = MiniMessage.miniMessage();
		try {
			copyDefaultResource("velocity-config.yml", "config.yml");
			copyDefaultResource("announcements.yml", "announcements.yml");
			Config cfg = Config.load(new File(folder, "config.yml"));
			if (cfg == null) {
				throw new IOException("Failed to load Velocity configuration");
			}
			motd = cfg.getString("motd", "<gold>Welcome to MLMC!</gold>");
			motdDelaySeconds = Math.max(0, cfg.getInt("motd-delay-seconds", 3));
			hubServer = cfg.getString("hub-server", "hub");
			if (cfg.getSection("sql") != null) {
				SQLManager.load(cfg.getSection("sql"), logger);
			}
	        reload();
		} catch (IOException ex) {
			throw new IllegalStateException("Failed to initialize NeoCore for Velocity", ex);
		}
		
		joinPrefix = mini.deserialize("<dark_gray>[<green>+</green>] ");
		leavePrefix = mini.deserialize("<dark_gray>[<red>-</red>] ");

        CommandManager mngr = proxy.getCommandManager();
        mngr.register(CmdBroadcast.meta(mngr, this), new CmdBroadcast());
        mngr.register(CmdSilentBroadcast.meta(mngr, this), new CmdSilentBroadcast());
        mngr.register(CmdMutableBroadcast.meta(mngr, this), new CmdMutableBroadcast());
        mngr.register(CmdSilentMutableBroadcast.meta(mngr, this), new CmdSilentMutableBroadcast());
        mngr.register(CmdHub.meta(mngr, this), new CmdHub());
        mngr.register(CmdMotd.meta(mngr, this), new CmdMotd());
        mngr.register(CmdTp.meta(mngr, this), new CmdTp());
        mngr.register(CmdTphere.meta(mngr, this), new CmdTphere());
        mngr.register(CmdUptime.meta(mngr, this), new CmdUptime());
        mngr.register(CmdSendAll.meta(mngr, this), new CmdSendAll());
        mngr.register(CmdSend.meta(mngr, this), new CmdSend());
        mngr.register(CmdKickAll.meta(mngr, this), new CmdKickAll());
        proxy.getEventManager().register(this, new VelocityListener());
        proxy.getChannelRegistrar().register(IDENTIFIER);
    }
    
    public static ProxyServer proxy() {
    	return proxy;
    }
    
    public static Logger logger() {
    	return logger;
    }
    
    public static File folder() {
    	return folder;
    }
    
    private void reload() throws IOException {
    	announceCfg = Config.load(new File(folder, "announcements.yml"));
		if (announceCfg == null) {
			throw new IOException("Failed to load Velocity announcements");
		}
    	announceList = announceCfg.getStringList("announcements");
		if (announceList == null) {
			announceList = new ArrayList<>();
		}
		rebuildAnnouncements();
    }

	private static void rebuildAnnouncements() {
		Builder b = Component.text();
		if (announceList.size() > 0) {
			for (int i = 0; i < announceList.size(); i++) {
				b.append(Component.text("- ", NamedTextColor.GRAY))
				.append(Component.text(announceList.get(i) + (i + 1 == announceList.size() ? "" : "\n"), NamedTextColor.YELLOW));
			}
		}
		else {
			b.append(Component.text("- ", NamedTextColor.GRAY))
			.append(Component.text("None for now!", NamedTextColor.YELLOW));
		}
		announcements = b.build();
	}
    
    public static void sendMotd(CommandSource s) {
    	// First send top half of MOTD (Mostly static)
		s.sendMessage(mini.deserialize(motd.replace("%ONLINE%", Integer.toString(proxy.getPlayerCount()))));
		s.sendMessage(announcements);
    }

	public static long motdDelaySeconds() {
		return motdDelaySeconds;
	}

	public static String hubServer() {
		return hubServer;
	}
    
    public static void addAnnouncement(CommandSource s, String msg) {
    	announceList.add(msg);
    	announceCfg.set("announcements", announceList);
		announceCfg.save();
		rebuildAnnouncements();
    }
    
    public static boolean removeAnnouncement(CommandSource s, int idx) {
		if (idx < 0 || idx >= announceList.size()) {
			Util.msg(s, Component.text("No announcement exists at index " + idx, NamedTextColor.RED));
			return false;
		}
    	announceList.remove(idx);
    	announceCfg.set("announcements", announceList);
		announceCfg.save();
		rebuildAnnouncements();
		return true;
    }
	
	public static Connection getConnection(String user) throws SQLException {
		return SQLManager.getConnection(user);
	}

	@Subscribe
	public void onProxyShutdown(ProxyShutdownEvent e) {
		SQLManager.shutdown();
	}
	
	// All servers
	public static void sendPluginMessage(String[] msgs) {
		sendPluginMessage(proxy.getAllServers(), msgs, false);
	}
	
	public static void sendPluginMessage(String[] servers, String[] msgs) {
		ArrayList<RegisteredServer> list = new ArrayList<RegisteredServer>();
		for (String server : servers) {
			proxy.getServer(server).ifPresentOrElse(list::add,
					() -> logger.warning("[NeoCore] Cannot send proxy message to unknown server " + server));
		}
		sendPluginMessage(list, msgs, false);
	}
	
	public static void sendPluginMessage(Iterable<RegisteredServer> servers, String[] msgs) {
		sendPluginMessage(servers, msgs, false);
	}

	public static void sendPluginMessage(Iterable<RegisteredServer> servers, String[] msgs, boolean queue) {
		if (msgs.length == 0) {
			throw new IllegalArgumentException("A proxy message requires a type");
		}
		byte[] data = new ProxyMessage(msgs[0], Arrays.asList(msgs).subList(1, msgs.length)).encode();
		for (RegisteredServer server : servers) {
			if (!server.sendPluginMessage(IDENTIFIER, data)) {
				if (queue) {
					queueMessage(server, data);
				}
				else {
					logger.warning("[NeoCore] Could not deliver proxy message to "
							+ server.getServerInfo().getName() + " because it has no active connection");
				}
			}
		}
	}

	public static void flushQueuedMessages(RegisteredServer server) {
		String serverName = server.getServerInfo().getName();
		Queue<byte[]> queue = queuedMessages.get(serverName);
		if (queue == null) {
			return;
		}
		while (queue.peek() != null && server.sendPluginMessage(IDENTIFIER, queue.peek())) {
			queue.remove();
		}
		if (queue.isEmpty()) {
			queuedMessages.remove(serverName, queue);
		}
	}

	private static void queueMessage(RegisteredServer server, byte[] data) {
		String serverName = server.getServerInfo().getName();
		Queue<byte[]> queue = queuedMessages.computeIfAbsent(serverName, ignored -> new ConcurrentLinkedQueue<>());
		if (queue.size() >= MAX_QUEUED_MESSAGES_PER_SERVER) {
			logger.warning("[NeoCore] Dropped proxy message because the queue for " + serverName + " is full");
			return;
		}
		queue.add(data);
	}
	
	public static VelocityCore inst() {
		return inst;
	}
	
	public static void loadFiles(File load, FileLoader loader) {
		if (!load.exists()) {
			logger.warning("[VelocityCore] Failed to load file " + load.getPath() + ", file doesn't exist");
			return;
		}
		
		if (load.isDirectory()) {
			for (File file : load.listFiles()) {
				loadFiles(file, loader);
			}
		}
		else {
			Config cfg;
			cfg = Config.load(load);
			loader.load(cfg, load);
		}
	}

	private static void copyDefaultResource(String resourceName, String fileName) throws IOException {
		File destination = new File(folder, fileName);
		if (destination.exists()) {
			return;
		}
		Files.createDirectories(folder.toPath());
		try (InputStream input = VelocityCore.class.getClassLoader().getResourceAsStream(resourceName)) {
			if (input == null) {
				throw new IOException("Bundled resource not found: " + resourceName);
			}
			Files.copy(input, destination.toPath());
		}
	}
	
	@Subscribe
	public void onLogin(PostLoginEvent e) {
		Util.broadcast(joinPrefix.append(Component.text(e.getPlayer().getUsername(), NamedTextColor.GRAY)), false);
	}
	
	@Subscribe
	public void onLogout(DisconnectEvent e) {
		Util.broadcast(leavePrefix.append(Component.text(e.getPlayer().getUsername(), NamedTextColor.GRAY)), false);
	}
	
	public static MiniMessage miniMessage() {
		return mini;
	}
}
