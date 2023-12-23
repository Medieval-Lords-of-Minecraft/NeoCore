package me.neoblade298.neocore.bukkit.particles;

import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.player.PlayerTags;

public class ParticleUtil {
	private static final int MAX_VIEW_DISTANCE = 32;
	private static final PlayerTags tags;
	
	static {
		tags = NeoCore.getNeoCoreTags();
	}
	
	public static LinkedList<Player> drawLine(ParticleContainer particle, Location l1, Location l2, double metersPerParticle) {
		return drawLineWithCache(calculateCache(l1), particle, l1, l2, metersPerParticle);
	}
	
	public static LinkedList<Location> calculateLine(Location l1, Location l2, double metersPerParticle) {
		return calculateLine(l1, l2, metersPerParticle, false);
	}
	
	public static LinkedList<Location> calculateLine(Location l1, Location l2, double metersPerParticle, boolean removeEdges) {
		Location start = l1.clone();
		Location end = l2.clone();
	    
		Vector v = end.clone().subtract(start).toVector();
		int iterations = (int) (v.length() / metersPerParticle);
		double lengthPerIteration = v.length() / iterations; // Makes sure our iterations aren't short at the end
		v.normalize();
	    v.multiply(lengthPerIteration);
	    if (v.length() == 0) {
	    	Bukkit.getLogger().warning("[NeoCore] Failed to draw particle line, vector length was 0");
	    	return null;
	    }

	    LinkedList<Location> locations = new LinkedList<Location>();
	    if (!removeEdges) locations.add(start.clone());
		for (int i = 0; i < iterations; i++) {
		    start.add(v);
		    locations.add(start.clone());
		}
		if (removeEdges) locations.removeLast();
		return locations;
	}
	
	public static LinkedList<Player> drawLineWithCache(LinkedList<Player> cache, ParticleContainer particle, Location l1, Location l2, double metersPerParticle) {
		particle.spawnWithCache(cache, l1);
		particle.spawnWithCache(cache, l2);
		for (Location loc : calculateLine(l1, l2, metersPerParticle)) {
			particle.spawnWithCache(cache, loc);
		}
		return cache;
	}
	
	public static LinkedList<Player> calculateCache(Location loc) {
		LinkedList<Player> list = new LinkedList<Player>();
		for (Player p : loc.getNearbyPlayers(MAX_VIEW_DISTANCE)) {
			if (tags.exists("hide-particles", p.getUniqueId())) continue;
			list.add(p);
		}
		return list;
	}
}
