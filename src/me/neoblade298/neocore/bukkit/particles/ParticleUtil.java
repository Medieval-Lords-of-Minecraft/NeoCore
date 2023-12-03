package me.neoblade298.neocore.bukkit.particles;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class ParticleUtil {
	public static void drawLine(ParticleContainer particle, Location l1, Location l2, double blocksPerParticle) {
		Location start = l1.clone();
		Location end = l2.clone();
	    
		Vector v = end.subtract(start).toVector();
		int iterations = (int) (v.length() / blocksPerParticle);
		v.normalize();
	    v.multiply(blocksPerParticle);
	    if (v.length() == 0) {
	    	Bukkit.getLogger().warning("[NeoCore] Failed to draw particle line, vector length was 0");
	    	return;
	    }

	    particle.spawn(start);
		for (int i = 0; i < iterations; i++) {
		    start.add(v);
		    particle.spawn(start);
		}
		particle.spawn(end);
	}
}
