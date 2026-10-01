package me.neoblade298.neocore.bukkit.effects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.player.PlayerTags;

public abstract class Effect {
	private static final int MAX_VIEW_DISTANCE = 32;
	protected static final PlayerTags tags;

	protected Audience forceVisibility;
	private String tagPrefix;

	static {
		tags = NeoCore.getNeoCoreTags();
	}
	
	public Effect(String tagPrefix) {
		this(tagPrefix, Audience.NONE);
	}
	
	public Effect(String tagPrefix, Audience forceVisibility) {
		this.tagPrefix = tagPrefix;
		this.forceVisibility = forceVisibility;
	}
	
	public Audience getForcedVisibility() {
		return forceVisibility;
	}

	protected abstract void playEffect(Player p, Location loc);
	protected abstract void playEffect(Location loc);
	public void play(Entity loc) {
		playEffect(loc.getLocation());
	}
	public void play(Location loc) {
		playEffect(loc);
	}
	public void play(Player origin, Entity loc, Audience audience) {
		play(origin, loc.getLocation(), audience);
	}
	public void play(Player origin, Entity loc) {
		play(origin, loc.getLocation(), Audience.ALL);
	}
	public void play(Player origin, Location loc) {
		play(origin, loc, Audience.ALL);
	}
	public void play(Player origin, Location loc, Audience audience) {
		switch (audience) {
		case ORIGIN:
			playForOrigin(origin, loc);
			break;
		case NOT_ORIGIN:
			playForNotOrigin(origin, loc);
			break;
		case ALL:
			playWithCache(calculateCache(origin, loc, forceVisibility, tagPrefix), loc);
			break;
		default:
			break;
		}
	}
	
	public void playWithCache(List<Player> cache, Location loc) {
		for (Player player : cache) {
			playEffect(player, loc);
		}
	}

	public void playWithCache(Collection<? extends Player> cache, Location loc) {
		playWithCache(new ArrayList<Player>(cache), loc);
	}
	private void playForOrigin(Player origin, Location loc) {
		if (!forceVisibility.containsOrigin() && Effect.tags.exists(tagPrefix + "-self", origin.getUniqueId())) return;
		playEffect(origin, loc);
	}
	
	private void playForNotOrigin(Player origin, Location loc) {
		List<Player> cache = calculateCache(origin, loc, forceVisibility);
		cache.remove(origin);
		playWithCache(cache, loc);
	}
	
	public List<Player> calculateCache(Player origin, Location loc, Audience forceVisibility) {
		return calculateCache(origin, loc, forceVisibility, MAX_VIEW_DISTANCE);
	}
	
	public List<Player> calculateCache(Player origin, Location loc, Audience forceVisibility, int viewDistance) {
		return calculateCache(origin, loc, forceVisibility, tagPrefix, viewDistance);
	}
	public static List<Player> calculateCache(Player origin, Location loc, Audience forceVisibility, String tagPrefix) {
		return calculateCache(origin, loc, forceVisibility, tagPrefix, MAX_VIEW_DISTANCE);
	}
	public static List<Player> calculateCache(Location loc) {
		return calculateCache(loc, MAX_VIEW_DISTANCE);
	}
	public static List<Player> calculateCache(Location loc, int viewDistance) {
		return new ArrayList<Player>(loc.getNearbyPlayers(viewDistance));
	}
	
	public static List<Player> calculateCache(Player origin, Location loc, Audience forceVisibility, String tagPrefix, int viewDistance) {
		List<Player> list = new ArrayList<Player>();
		
		if (origin == null) {
			// Defaults to force all
			list.addAll(loc.getNearbyPlayers(viewDistance));
			return list;
		}
		
		for (Player p : loc.getNearbyPlayers(viewDistance)) {
			if (p == origin) {
				if (!forceVisibility.containsOrigin() && tags.exists(tagPrefix + "-self", p.getUniqueId())) continue;
			}
			else {
				if (!forceVisibility.containsNonOrigin() && tags.exists(tagPrefix + "-other", p.getUniqueId())) continue;
			}
			list.add(p);
		}
		return list;
	}
}
