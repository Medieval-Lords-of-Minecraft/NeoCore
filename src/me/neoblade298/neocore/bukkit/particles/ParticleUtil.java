package me.neoblade298.neocore.bukkit.particles;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ParticleUtil {
	public static ArrayList<Player> drawLine(ParticleContainer particle, Location l1, Location l2, double blocksPerParticle) {
		return drawLineWithCache(null, particle, l1, l2, blocksPerParticle);
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

	    ArrayList<Player> temp = particle.spawn(start);
	    if (cache == null) cache = temp;
		for (int i = 0; i < iterations; i++) {
		    start.add(v);
		    particle.spawnWithCache(cache, start);
		}
		particle.spawnWithCache(cache, end);
		return cache;
	}
}
