package me.neoblade298.neocore.bungee;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Logger;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.plugin.Plugin;

import me.neoblade298.neocore.bungee.messaging.MessagingManager;
import me.neoblade298.neocore.bungee.chat.ChatResponseHandler;
import me.neoblade298.neocore.bungee.commands.builtin.*;
import me.neoblade298.neocore.bungee.io.FileLoader;
import me.neoblade298.neocore.bungee.listeners.ChatListener;
import me.neoblade298.neocore.bungee.listeners.MainListener;
import me.neoblade298.neocore.shared.exceptions.NeoIOException;
import me.neoblade298.neocore.shared.io.SQLManager;
import me.neoblade298.neocore.shared.util.GradientManager;
import me.neoblade298.neocore.shared.util.SharedUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

@Plugin(id = "neocore", name = "NeoCore", version = "0.1.0-SNAPSHOT",
        authors = {"Ascheladd"})
public class BungeeCore {
	private static ProxyServer proxy;
	private static Logger logger;
	private static BungeeCore inst;
	private static BaseComponent[] motd;
	private static List<String> announcements = new ArrayList<String>();
	private static Configuration announceyml;
	
	// Used for tab complete
	public static TreeSet<String> players = new TreeSet<String>();
	
	public BungeeCore(ProxyServer server, Logger logger) {
		BungeeCore.proxy = server;
		BungeeCore.logger = logger;
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
        getProxy().registerChannel("neocore:bungee");
        
        // messaging
        try {
			MessagingManager.reload();
		} catch (NeoIOException ex) {
			ex.printStackTrace();
		}
        
        // gradients
        try {
			GradientManager.load(ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File("/home/MLMC/Resources/shared/NeoCore/gradients.yml")));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
        
        // sql
		try {
			Configuration cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
	        SQLManager.load(cfg.getSection("sql"));
	        reload();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static ProxyServer getProxy() {
    	return proxy;
    }
    
    public static Logger getLogger() {
    	return logger;
    }
    
    private void reload() throws IOException {
    	loadFiles(new File(this.getDataFolder(), "motd.yml"), (yml, cfg) -> {
    		motd = MessagingManager.parseMessage(yml.getSection("motd"));
    	});
    	announceyml = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "announcements.yml"));
    	announcements = announceyml.getStringList("announcements");
    }
    
    public static void sendMotd(CommandSender s) {
		BaseComponent[] msg = new BaseComponent[motd.length + (announcements.size() > 0 ? announcements.size() : 1)];
		int idx = 0;
		for (BaseComponent comp : motd) {
			BaseComponent dupe = comp.duplicate();
			msg[idx++] = dupe;
			if (dupe instanceof TextComponent) {
				TextComponent text = (TextComponent) dupe;
				text.setText(text.getText().replaceAll("%ONLINE%", "" + inst.getProxy().getOnlineCount()));
			}
		}
		if (announcements.size() > 0) {
			for (int i = 0; i < announcements.size(); i++) {
				msg[idx++] = new TextComponent(SharedUtil.translateColors("§6- §e" + announcements.get(i) + (i + 1 == announcements.size() ? "" : "\n")));
			}
		}
		else {
			msg[idx++] = new TextComponent(SharedUtil.translateColors("&6- &eNone for now!"));
		}
		s.sendMessage(msg);
    }
    
    public static void addMotd(CommandSender s, String msg) {
    	announcements.add(msg);
    	announceyml.set("announcements", announcements);
    	try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(announceyml, new File(inst.getDataFolder(), "announcements.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void removeMotd(CommandSender s, int idx) {
    	announcements.remove(idx);
    	announceyml.set("announcements", announcements);
    	try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(announceyml, new File(inst.getDataFolder(), "announcements.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	public static Connection getConnection(String user) {
		return SQLManager.getConnection(user);
	}
	
	// All servers
	public static void sendPluginMessage(String[] msgs) {
		sendPluginMessage(inst.getProxy().getServers().values(), msgs, false);
	}
	
	public static void sendPluginMessage(String[] msgs, boolean queueMessage) {
		sendPluginMessage(inst.getProxy().getServers().values(), msgs, queueMessage);
	}
	
	public static void sendPluginMessage(String[] servers, String[] msgs, boolean queueMessage) {
		ArrayList<ServerInfo> list = new ArrayList<ServerInfo>();
		for (String server : servers) {
			list.add(inst.getProxy().getServerInfo(server));
		}
		sendPluginMessage(list, msgs, queueMessage);
	}
	
	public static void sendPluginMessage(Iterable<ServerInfo> servers, String[] msgs, boolean queueMessage) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		for (String msg : msgs) {
			out.writeUTF(msg);
		}
		for (ServerInfo server : servers) {
			server.sendData("neocore:bungee", out.toByteArray(), queueMessage);
		}
	}
	
	public static BungeeCore inst() {
		return inst;
	}
	
	public static void promptChatResponse(ProxiedPlayer p, ChatResponseHandler... handler) {
		ChatListener.addChatHandler(p, 30, handler);
	}
	
	public static void promptChatResponse(ProxiedPlayer p, int timeoutSeconds, ChatResponseHandler... handler) {
		ChatListener.addChatHandler(p, timeoutSeconds, handler);
	}
	
	public static void loadFiles(File load, FileLoader loader) {
		if (!load.exists()) {
			inst.getProxy().getLogger().warning("[BungeeCore] Failed to load file " + load.getPath() + ", file doesn't exist");
			return;
		}
		
		if (load.isDirectory()) {
			for (File file : load.listFiles()) {
				loadFiles(file, loader);
			}
		}
		else {
			Configuration cfg;
			try {
				cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(load);
				loader.load(cfg, load);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
