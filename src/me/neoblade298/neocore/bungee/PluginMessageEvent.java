package me.neoblade298.neocore.bungee;

import java.util.ArrayList;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PluginMessageEvent extends Event {
	private String channel;
    private static final HandlerList handlers = new HandlerList();
    private ArrayList<String> msgs = new ArrayList<String>();
	
	public PluginMessageEvent(String channel, ArrayList<String> msgs) {
		this.channel = channel;
		this.msgs = msgs;
	}

	public String getChannel() {
		return channel;
	}
	
	public ArrayList<String> getMessages() {
		return msgs;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

    public static HandlerList getHandlerList() {
        return handlers;
    }
	
}
