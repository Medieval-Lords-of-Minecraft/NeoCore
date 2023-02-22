package me.neoblade298.neocore.bungee.listeners;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import me.neoblade298.neocore.bungee.BungeeCore;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class MainListener implements Listener {
    @EventHandler
    public void onJoin(PostLoginEvent e) {
    	e.
    	e.getPlayer().hasPermission(permission)
    	BungeeCore.players.add(e.getPlayer().getName());
    	BungeeCore.inst().getProxy().getScheduler().schedule(BungeeCore.inst(), () -> {
    		BungeeCore.sendMotd(e.getPlayer());
		}, 3, TimeUnit.SECONDS);
    }
    
    @EventHandler
    public void onLeave(PlayerDisconnectEvent e) {
    	BungeeCore.players.remove(e.getPlayer().getName());
    }
    
    @EventHandler
    public void onBungeeMessage(PluginMessageEvent e) {
		ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
		try {
			if (in.readUTF().equals("NeoCore")) {
				String read = in.readUTF();
				switch (read) {
				case "cmd":
					BungeeCore.inst().getProxy().getPluginManager().dispatchCommand(BungeeCore.inst().getProxy().getConsole(), in.readUTF());
					break;
				case "fwd":
					handleForwardMessage(in, false);
					break;
				case "fwdq":
					handleForwardMessage(in, true);
					break;
				}
			}
		} catch (Exception ex) {	}
    }
    
    private void handleForwardMessage(ByteArrayDataInput in, boolean queue) {
    	String from = in.readUTF();
    	ArrayList<String> servers = new ArrayList<String>();
    	String server = in.readUTF();
    	boolean sendToAll = false;
    	boolean sendToOthers = false;
    	while (!server.equals("EOP")) {
    		if (server.equals("ALL")) {
    			sendToAll = true;
    		}
    		else if (server.equals("OTHER")) {
    			sendToOthers = true;
    		}
    		servers.add(server);
    		server = in.readUTF();
    	}
    	
    	ArrayList<String> msgs = new ArrayList<String>(); // Channel is the first one
    	String msg = in.readUTF();
    	while (!msg.equals("EOP")) {
    		msgs.add(msg);
    		msg = in.readUTF();
    	}
    	
    	if (sendToAll) {
        	BungeeCore.sendPluginMessage(arrToList(msgs), queue);
    	}
    	else if (sendToOthers) {
        	BungeeCore.sendPluginMessage(getAllOtherServers(from), (String[]) msgs.toArray(), queue);
    	}
    	else {
    		BungeeCore.sendPluginMessage((String[]) servers.toArray(), (String[]) msgs.toArray(), queue);
    	}
    }
    
    private String[] getAllOtherServers(String except) {
    	ProxyServer proxy = BungeeCore.inst().getProxy();
    	String[] servers = new String[proxy.getServers().size() - 1];
    	
    	int count = 0;
    	for (ServerInfo info : proxy.getServers().values()) {
    		if (info.getName().equalsIgnoreCase(except)) continue;
    		servers[count++] = info.getName();
    	}
    	return servers;
    }
    
    private String[] arrToList(ArrayList<String> arr) {
    	String[] list = new String[arr.size()];
    	int count = 0;
    	for (String i : arr) {
    		list[count++] = i;
    	}
    	return list;
    }
}
