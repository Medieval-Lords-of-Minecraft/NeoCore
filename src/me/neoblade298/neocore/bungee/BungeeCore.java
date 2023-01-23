package me.neoblade298.neocore.bungee;

import java.io.File;
import java.io.IOException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.neoblade298.neocore.bungee.commands.*;
import me.neoblade298.neocore.bungee.io.FileLoader;
import me.neoblade298.neocore.shared.io.SQLManager;
import me.neoblade298.neocore.shared.messaging.MessagingManager;
import me.neoblade298.neocore.shared.util.SharedUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

public class BungeeCore extends Plugin implements Listener
{
	private static BungeeCore inst;
	private static BaseComponent[] motd;
	private static List<String> announcements = new ArrayList<String>();
	private static Configuration announceyml;
	
    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerCommand(this, new CmdHub());
        getProxy().getPluginManager().registerCommand(this, new CmdMotd());
        getProxy().getPluginManager().registerCommand(this, new CmdTp());
        getProxy().getPluginManager().registerCommand(this, new CmdTphere());
        getProxy().getPluginManager().registerCommand(this, new CmdUptime());
        getProxy().getPluginManager().registerCommand(this, new CmdSendAll());
        getProxy().getPluginManager().registerListener(this, this);
        getProxy().registerChannel("neocore:bungee");
        
        inst = this;
        
        Configuration cfg = null;
		try {
			cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
	        SQLManager.load(cfg.getSection("sql"));
	        reload();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void reload() throws IOException {
    	loadFiles(new File(this.getDataFolder(), "motd.yml"), (yml, cfg) -> {
    		motd = MessagingManager.parseMessage(yml.getSection("motd"));
    	});
    	announceyml = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "announcements.yml"));
    	announcements = announceyml.getStringList("announcements");
    }
    
    @EventHandler
    public void onJoin(PostLoginEvent e) {
    	getProxy().getScheduler().schedule(this, () -> {
    		sendMotd(e.getPlayer());
		}, 3, TimeUnit.SECONDS);
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
    
    @EventHandler
    public void onBungeeMessage(PluginMessageEvent e) {
		ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
		try {
			if (in.readUTF().equals("NeoCore")) {
				if (in.readUTF().equals("cmd")) {
					getProxy().getPluginManager().dispatchCommand(getProxy().getConsole(), in.readUTF());
				}
			}
		} catch (Exception ex) {	}
    }
	
	public static Statement getDefaultStatement() {
		return SQLManager.getDefaultStatement();
	}
	
	public static Statement getStatement(String user) {
		return SQLManager.getStatement(user);
	}
	
	public static void sendPluginMessage(String[] servers, String[] msgs) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		for (String msg : msgs) {
			out.writeUTF(msg);
		}
		for (String key : servers) {
			inst.getProxy().getServerInfo(key).sendData("neocore:bungee", out.toByteArray());
		}
	}
	
	public static void sendPluginMessage(ServerInfo[] servers, String[] msgs) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		for (String msg : msgs) {
			out.writeUTF(msg);
		}
		for (ServerInfo server : servers) {
			server.sendData("neocore:bungee", out.toByteArray());
		}
	}
	
	public static BungeeCore inst() {
		return inst;
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
