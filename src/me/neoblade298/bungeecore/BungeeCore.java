package me.neoblade298.bungeecore;

import java.io.File;
import java.sql.Statement;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.neoblade298.bungeecore.commands.*;
import me.neoblade298.neocore.io.IOComponent;
import me.neoblade298.neocore.io.IOComponentWrapper;
import me.neoblade298.neocore.io.IOManager;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class BungeeCore extends Plugin implements Listener
{
	private static BungeeCore inst;
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
        
        // Bungee IO - Essentially just used for sql
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "config.yml"));
		new IOManager(cfg);
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
}
