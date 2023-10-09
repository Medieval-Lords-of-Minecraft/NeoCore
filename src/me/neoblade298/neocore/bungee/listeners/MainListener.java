package me.neoblade298.neocore.bungee.listeners;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import me.neoblade298.neocore.bungee.BungeeCore;

public class MainListener {
    @Subscribe
    public void onJoin(PostLoginEvent e) {
    	BungeeCore.players.add(e.getPlayer().getUsername());
    	BungeeCore.proxy().getScheduler().buildTask(BungeeCore.inst(), () -> {
    		BungeeCore.sendMotd(e.getPlayer());
		}).delay(3L, TimeUnit.SECONDS);
    }
    
    @Subscribe
    public void onLeave(DisconnectEvent e) {
    	BungeeCore.players.remove(e.getPlayer().getUsername());
    }
    
    @Subscribe
    public void onBungeeMessage(PluginMessageEvent e) {
		ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
		try {
			if (in.readUTF().equals("NeoCore")) {
				String read = in.readUTF();
				switch (read) {
				case "cmd":
					BungeeCore.proxy().getCommandManager().executeImmediatelyAsync(BungeeCore.proxy().getConsoleCommandSource(), in.readUTF());
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
        	BungeeCore.sendPluginMessage(arrToList(msgs));
    	}
    	else if (sendToOthers) {
        	BungeeCore.sendPluginMessage(getAllOtherServers(from), (String[]) msgs.toArray());
    	}
    	else {
    		BungeeCore.sendPluginMessage((String[]) servers.toArray(), (String[]) msgs.toArray());
    	}
    }
    
    private String[] getAllOtherServers(String except) {
    	ProxyServer proxy = BungeeCore.proxy();
    	String[] servers = new String[proxy.getAllServers().size() - 1];
    	
    	int count = 0;
    	for (RegisteredServer server : proxy.getAllServers()) {
    		if (server.getServerInfo().getName().equalsIgnoreCase(except)) continue;
    		servers[count++] = server.getServerInfo().getName();
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
