package me.neoblade298.neocore.bukkit.particles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ParticleUtil {
	public static void drawLine(Player p, Location l1, Location l2, Particle part, boolean showAllPlayers, int perPoint, double offset, double speed, double blocksPerParticle) {
		drawLine(p, l1, l2, part, showAllPlayers, perPoint, offset, speed, blocksPerParticle, null);
	}

	public static void drawLine(Player p, Location l1, Location l2, Particle part, boolean showAllPlayers, int perPoint, double offset, double speed, double blocksPerParticle, DustOptions data) { 
		Location start = l1.clone();
		Location end = l2.clone();
	    
		Vector v = end.subtract(start).toVector();
		int iterations = (int) (v.length() / blocksPerParticle);
		for (int i = 1; i < iterations; i++) {
		    v.normalize();
		    v.multiply(i * blocksPerParticle);
		    start.add(v);
			spawnParticle(p, showAllPlayers, start, part, perPoint, offset, offset, offset, speed, data); // Data is nullable
			start.subtract(v);
		}
	}
	
	public static void spawnParticle(Player p, boolean showAllPlayers, Location loc, Particle part, int amount, double xOff, double yOff, double zOff, double speed, DustOptions data) {
		if (showAllPlayers) {
			p.getWorld().spawnParticle(part, loc, amount, xOff, yOff, zOff, speed, data);
		}
		else {
			p.spawnParticle(part, loc, amount, xOff, yOff, zOff, speed, data);
		}
	}
}
