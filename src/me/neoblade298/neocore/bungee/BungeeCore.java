package me.neoblade298.neocore.bungee;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.yaml.snakeyaml.Yaml;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.*;
import me.neoblade298.neocore.bungee.chat.ChatResponseHandler;
import me.neoblade298.neocore.bungee.commands.builtin.*;
import me.neoblade298.neocore.bungee.io.FileLoader;
import me.neoblade298.neocore.bungee.listeners.ChatListener;
import me.neoblade298.neocore.bungee.listeners.MainListener;
import me.neoblade298.neocore.shared.chat.MiniMessageManager;
import me.neoblade298.neocore.shared.exceptions.NeoIOException;
import me.neoblade298.neocore.shared.io.Config;
import me.neoblade298.neocore.shared.io.SQLManager;
import me.neoblade298.neocore.shared.util.GradientManager;
import me.neoblade298.neocore.shared.util.SharedUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

@Plugin(id = "neocore", name = "NeoCore", version = "0.1.0-SNAPSHOT",
        authors = {"Ascheladd"})
public class BungeeCore {
	public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from("neocore:bungee");
	private static ProxyServer proxy;
	private static Logger logger;
	private static BungeeCore inst;
	private static TextComponent motd;
	private static List<String> announcements = new ArrayList<String>();
	private static Config announceCfg;
	private static File folder;
	
	// Used for tab complete
	public static TreeSet<String> players = new TreeSet<String>();
	
	public BungeeCore(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
		BungeeCore.proxy = server;
		BungeeCore.logger = logger;
		folder = dataDirectory.toFile();
	}
	
    public void onProxyInitialization(ProxyInitializeEvent e) {
        inst = this;
        CommandManager mngr = proxy.getCommandManager();
        
        mngr.register(CmdBroadcast.meta(mngr, this), new CmdBroadcast());
        getProxy().getPluginManager().register(this, new CmdSilentBroadcast());
        getProxy().getPluginManager().register(this, new CmdMutableBroadcast());
        getProxy().getPluginManager().register(this, new CmdSilentMutableBroadcast());
        getProxy().getPluginManager().register(this, new CmdHub());
        getProxy().getPluginManager().register(this, new CmdMotd());
        getProxy().getPluginManager().register(this, new CmdTp());
        getProxy().getPluginManager().register(this, new CmdTphere());
        getProxy().getPluginManager().register(this, new CmdUptime());
        getProxy().getPluginManager().register(this, new CmdSendAll());
        getProxy().getPluginManager().register(this, new CmdKickAll());
        getProxy().getPluginManager().registerListener(this, new MainListener());
        getProxy().getPluginManager().registerListener(this, new ChatListener());
        proxy.getChannelRegistrar().register(IDENTIFIER);
        
        // gradients
        try {
			GradientManager.load(Config.load(new File("/home/MLMC/Resources/shared/NeoCore/gradients.yml")));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
        
        // sql
		try {
			Config cfg = Config.load(new File(folder, "config.yml"));
	        SQLManager.load(cfg.getSection("sql"));
	        reload();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
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
    	MiniMessageManager.reload();
    	announceCfg = Config.load(new File(folder, "announcements.yml"));
    	announcements = announceCfg.getStringList("announcements");
    }
    
    public static void sendMotd(CommandSource s) {
		String[] msg = new String[motd.length + (announcements.size() > 0 ? announcements.size() : 1)];
		int idx = 0;
		for (String comp : motd) {
			msg[idx++] = comp;
			comp.replaceAll("%ONLINE", "" + proxy.getPlayerCount());
		}
		if (announcements.size() > 0) {
			for (int i = 0; i < announcements.size(); i++) {
				msg[idx++] = SharedUtil.translateColors("§6- §e" + announcements.get(i) + (i + 1 == announcements.size() ? "" : "\n"));
			}
		}
		else {
			msg[idx++] = SharedUtil.translateColors("&6- &eNone for now!");
		}
		
		for (String m : msg) {
			s.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(m));
		}
    }
    
    public static void addAnnouncement(CommandSource s, String msg) {
    	announcements.add(msg);
    	announceCfg.set("announcements", announcements);
		announceCfg.save();
    }
    
    public static void removeAnnouncement(CommandSource s, int idx) {
    	announcements.remove(idx);
    	announceCfg.set("announcements", announcements);
		announceCfg.save();
    }
	
	public static Connection getConnection(String user) {
		return SQLManager.getConnection(user);
	}
	
	// All servers
	public static void sendPluginMessage(String[] msgs) {
		sendPluginMessage(proxy.getAllServers(), msgs);
	}
	
	public static void sendPluginMessage(String[] servers, String[] msgs) {
		ArrayList<RegisteredServer> list = new ArrayList<RegisteredServer>();
		for (String server : servers) {
			list.add(proxy.getServer(server).get());
		}
		sendPluginMessage(list, msgs);
	}
	
	public static void sendPluginMessage(Iterable<RegisteredServer> servers, String[] msgs) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		for (String msg : msgs) {
			out.writeUTF(msg);
		}
		for (RegisteredServer server : servers) {
			server.sendPluginMessage(IDENTIFIER, out.toByteArray());
		}
	}
	
	public static BungeeCore inst() {
		return inst;
	}
	
	public static void promptChatResponse(Player p, ChatResponseHandler... handler) {
		ChatListener.addChatHandler(p, 30, handler);
	}
	
	public static void promptChatResponse(Player p, int timeoutSeconds, ChatResponseHandler... handler) {
		ChatListener.addChatHandler(p, timeoutSeconds, handler);
	}
	
	public static void loadFiles(File load, FileLoader loader) {
		if (!load.exists()) {
			logger.warning("[BungeeCore] Failed to load file " + load.getPath() + ", file doesn't exist");
			return;
		}
		
		if (load.isDirectory()) {
			for (File file : load.listFiles()) {
				loadFiles(file, loader);
			}
		}
		else {
			Config cfg;
			try {
				cfg = Config.load(load);
				loader.load(cfg, load);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
