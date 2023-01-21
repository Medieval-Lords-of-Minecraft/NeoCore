package me.neoblade298.neocore.bukkit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.neoblade298.neocore.bukkit.player.Value;

public class PlayerFieldChangedEvent extends Event {
	private Player p;
	private String key, subkey;
	private Value v;
	private ValueChangeType type;
    private static final HandlerList handlers = new HandlerList();

	public PlayerFieldChangedEvent(Player p, String key, String subkey, Value v, ValueChangeType type) {
		this.p = p;
		this.key = key;
		this.subkey = subkey;
		this.v = v;
		this.type = type;
	}
	
	public ValueChangeType getType() {
		return type;
	}

	public Player getPlayer() {
		return p;
	}

	public String getKey() {
		return key;
	}

	public String getSubkey() {
		return subkey;
	}

	public Value getValue() {
		return v;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
