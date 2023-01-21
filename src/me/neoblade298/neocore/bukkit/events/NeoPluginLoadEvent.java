package me.neoblade298.neocore.bukkit.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NeoPluginLoadEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String msg;
	public NeoPluginLoadEvent(String msg) {
		this.msg = msg;
	}
	public String getMessage() {
		return msg;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
