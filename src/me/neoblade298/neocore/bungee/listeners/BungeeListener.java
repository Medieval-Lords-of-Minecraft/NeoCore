package me.neoblade298.neocore.bungee.listeners;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import me.neoblade298.neocore.bungee.BungeeCore;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BungeeListener implements Listener {
    
    @EventHandler
    public void onJoin(PostLoginEvent e) {
    	BungeeCore.inst().getProxy().getScheduler().schedule(BungeeCore.inst(), () -> {
    		BungeeCore.sendMotd(e.getPlayer());
		}, 3, TimeUnit.SECONDS);
    }
    
    @EventHandler
    public void onBungeeMessage(PluginMessageEvent e) {
		ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
		try {
			if (in.readUTF().equals("NeoCore")) {
				switch (in.readUTF()) {
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
    	while (!server.equals("end")) {
    		if (server.equals("all")) {
    			sendToAll = true;
    		}
    		servers.add(server);
    		server = in.readUTF();
    	}
    	
    	ArrayList<String> msgs = new ArrayList<String>(); // Channel is the first one
    	String msg = in.readUTF();
    	while (!server.equals("EOP")) {
    		msgs.add(msg);
    		msg = in.readUTF();
    	}
    	
    	if (sendToAll) {
        	BungeeCore.sendPluginMessage((String[]) msgs.toArray(), queue);
    	}
    	else {
    		BungeeCore.sendPluginMessage((String[]) servers.toArray(), (String[]) msgs.toArray(), queue);
    	}
    }
}
