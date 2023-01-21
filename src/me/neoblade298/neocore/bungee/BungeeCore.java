package me.neoblade298.neocore.bungee;

import java.io.File;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.io.IOComponent;
import me.neoblade298.neocore.bukkit.io.IOComponentWrapper;
import me.neoblade298.neocore.bukkit.io.PlayerIOManager;
import me.neoblade298.neocore.bungee.commands.*;
import me.neoblade298.neocore.shared.exceptions.NeoIOException;
import me.neoblade298.neocore.shared.io.FileLoader;
import me.neoblade298.neocore.shared.io.SQLManager;
import me.neoblade298.neocore.shared.messaging.MessagingManager;
import me.neoblade298.neocore.util.Util;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class BungeeCore extends Plugin implements Listener
{
	private static BungeeCore inst;
	private static BaseComponent[] motd;
	
    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerCommand(this, new CmdHub());
        getProxy().getPluginManager().registerCommand(this, new CmdTp());
        getProxy().getPluginManager().registerCommand(this, new CmdTphere());
        getProxy().getPluginManager().registerCommand(this, new CmdUptime());
        getProxy().getPluginManager().registerCommand(this, new CmdSendAll());
        getProxy().getPluginManager().registerListener(this, this);
        getProxy().registerChannel("neocore:bungee");
        
        inst = this;
        reload();
        
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "config.yml"));
		new PlayerIOManager(cfg);
    }
    
    private void reload() {
    	loadFiles(new File(this.getDataFolder(), "motd.yml"), (yml, cfg) -> {
    		MessagingManager.parseMessage(yml.getConfigurationSection("motd"));
    	});
    }
    
    @EventHandler
    public void onJoin(PostLoginEvent e) {
    	e.getPlayer().sendMessage(motd);
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
	
	public static IOComponentWrapper registerIOComponent(JavaPlugin plugin, IOComponent component, String key, int priority) {
		return PlayerIOManager.register(plugin, component, key, priority);
	}
	
	public static IOComponentWrapper registerIOComponent(JavaPlugin plugin, IOComponent component, String key) {
		return PlayerIOManager.register(plugin, component, key, 0);
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
			YamlConfiguration cfg = YamlConfiguration.loadConfiguration(load);
			loader.load(cfg, load);
		}
	}
}
