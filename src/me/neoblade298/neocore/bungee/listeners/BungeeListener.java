package me.neoblade298.neocore.bungee.listeners;

import java.util.concurrent.TimeUnit;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import me.neoblade298.neocore.bungee.BungeeCore;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
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
				if (in.readUTF().equals("cmd")) {
					BungeeCore.inst().getProxy().getPluginManager().dispatchCommand(BungeeCore.inst().getProxy().getConsole(), in.readUTF());
				}
			}
		} catch (Exception ex) {	}
    }
}
