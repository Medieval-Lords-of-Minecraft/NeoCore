package me.neoblade298.neocore.bukkit.particles;

import java.util.ArrayList;

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
	
	public static ArrayList<Player> drawLine(ParticleContainer particle, Location l1, Location l2, double blocksPerParticle) {
		return drawLineWithCache(calculateCache(l1), particle, l1, l2, blocksPerParticle);
	}
	
	public static ArrayList<Player> drawLineWithCache(ArrayList<Player> cache, ParticleContainer particle, Location l1, Location l2, double blocksPerParticle) {
		Location start = l1.clone();
		Location end = l2.clone();
	    
		Vector v = end.subtract(start).toVector();
		int iterations = (int) (v.length() / blocksPerParticle);
		v.normalize();
	    v.multiply(blocksPerParticle);
	    if (v.length() == 0) {
	    	Bukkit.getLogger().warning("[NeoCore] Failed to draw particle line, vector length was 0");
	    	return null;
	    }

	    particle.spawnWithCache(cache, start);
		for (int i = 0; i < iterations; i++) {
		    start.add(v);
		    particle.spawnWithCache(cache, start);
		}
		particle.spawnWithCache(cache, end);
		return cache;
	}
	
	public static ArrayList<Player> calculateCache(Location loc) {
		ArrayList<Player> list = new ArrayList<Player>();
		for (Player p : loc.getNearbyPlayers(MAX_VIEW_DISTANCE)) {
			if (tags.exists("hide-particles", p.getUniqueId())) continue;
			list.add(p);
		}
		return list;
	}
}
