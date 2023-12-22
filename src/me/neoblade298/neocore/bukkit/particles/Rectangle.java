package me.neoblade298.neocore.bukkit.particles;

import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Rectangle extends ParticleShape2D {
	private static final double DEFAULT_METERS = 0.5;
	private double length, height, metersPerParticle;
	
	public Rectangle(double length, double height) {
		this(length, height, DEFAULT_METERS);
	}
	
	public Rectangle(double length, double height, double metersPerParticle) {
		this.length = length;
		this.height = height;
		this.metersPerParticle = metersPerParticle;
	}

	@Override
	public void drawWithCache(LinkedList<Player> cache, ParticleContainer particle, Location center, Vector localRight,
			Vector localUp, Vector localForward, ParticleContainer fill) {
		Location bl = center.add(localRight.clone().multiply(length * -0.5).add(localUp.clone().multiply(height * -0.5)));
		Location tl = bl.clone().add(localUp.clone().multiply(height));
		
		if (fill == null) {
			Location br = bl.clone().add(localRight.clone().multiply(length));
			Location tr = br.clone().add(localUp.clone().multiply(height));
			ParticleUtil.drawLineWithCache(cache, particle, bl, br, metersPerParticle);
			ParticleUtil.drawLineWithCache(cache, particle, br, tr, metersPerParticle);
			ParticleUtil.drawLineWithCache(cache, particle, tr, tl, metersPerParticle);
			ParticleUtil.drawLineWithCache(cache, particle, tl, bl, metersPerParticle);
		}
		else {
			Vector right = localRight.clone().multiply(length);
			for (Location upPoint : ParticleUtil.calculateLine(tl, bl, metersPerParticle)) {
				ParticleUtil.drawLineWithCache(cache, particle, upPoint, upPoint.clone().add(right), metersPerParticle);
			}
		}
	}
}
