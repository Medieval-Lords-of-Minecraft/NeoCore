package me.neoblade298.neocore.bukkit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NeoCoreLoadCompleteEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player p;
	public NeoCoreLoadCompleteEvent(Player p) {
		this.p = p;
	}
	public Player getPlayer() {
		return p;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
