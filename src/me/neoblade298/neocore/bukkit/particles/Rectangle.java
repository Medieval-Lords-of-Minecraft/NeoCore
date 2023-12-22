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
		ParticleShapeMemory mem = calculate(center, localRight, localUp, localForward);
		mem.draw(cache, particle, fill);
	}

	@Override
	public ParticleShapeMemory calculate(Location center, Vector localRight, Vector localUp, Vector localForward) {
		Location bl = center.add(localRight.clone().multiply(length * -0.5).add(localUp.clone().multiply(height * -0.5)));
		Location tl = bl.clone().add(localUp.clone().multiply(height));
		Location br = bl.clone().add(localRight.clone().multiply(length));
		Location tr = br.clone().add(localUp.clone().multiply(height));
		LinkedList<Location> edges = ParticleUtil.calculateLine(bl, br, metersPerParticle);
		edges.addAll(ParticleUtil.calculateLine(br, tr, metersPerParticle));
		edges.addAll(ParticleUtil.calculateLine(tr, tl, metersPerParticle));
		edges.addAll(ParticleUtil.calculateLine(tl, bl, metersPerParticle));

		Vector right = localRight.clone().multiply(length);
		LinkedList<Location> fill = new LinkedList<Location>();
		for (Location upPoint : ParticleUtil.calculateLine(tl, bl, metersPerParticle, true)) {
			fill.addAll(ParticleUtil.calculateLine(upPoint, upPoint.clone().add(right), metersPerParticle));
		}
		return new ParticleShapeMemory(center, edges, fill);
	}
}
