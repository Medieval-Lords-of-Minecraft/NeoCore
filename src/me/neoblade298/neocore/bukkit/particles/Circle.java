package me.neoblade298.neocore.bukkit.particles;

import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Circle extends ParticleShape2D {
	private static final double POINTS_PER_CIRCUMFERENCE = 5;
	
	private double radius, metersPerParticle;
	private int points;
	private LinkedList<Vector> cachedEdges, cachedFill; // For flat circles only
	
	public Circle(double radius, int points) {
		this.radius = radius;
		this.points = points;
	}
	
	public Circle(double radius) {
		this(radius, (int) (POINTS_PER_CIRCUMFERENCE * (Math.PI * radius * radius)));
	}

	@Override
	public void drawWithCache(LinkedList<Player> cache, ParticleContainer particle, Location center, Vector localRight,
			Vector localUp, Vector localForward, ParticleContainer fill) {
		
		// If circle is flat, no need to recreate circle
		if (localForward.getY() == 1 && cachedEdges != null && (fill == null || cachedFill != null)) {
			drawFlatWithCache(cache, center, fill);
			return;
		}
		
		double rotationPerPoint = (2 * Math.PI) / (double) points;
		Vector rotator = localUp.clone().multiply(radius);
		
		boolean cacheFlatEdges = cachedEdges == null && localForward.getY() == 1;
		if (cacheFlatEdges) cachedEdges = new LinkedList<Vector>();
		for (int i = 0; i < points; i++) {
			Location point = center.clone().add(rotator.rotateAroundAxis(localForward, rotationPerPoint));
			if (cacheFlatEdges) cachedEdges.add(rotator.clone());
			particle.spawnWithCache(cache, point);
		}

		boolean cacheFill = cachedFill == null && localForward.getY() == 1;
		if (cacheFill) cachedFill = new LinkedList<Vector>();
		if (fill != null) {
			Location topLeft = center.add(localRight.clone().multiply(radius)).add(localUp.clone().multiply(radius));
			Vector right = localUp.clone().multiply(radius * 2);
			double radiusSq = radius * radius;
			for (Location upPoint : ParticleUtil.calculateLine(topLeft, topLeft.clone().add(localUp.clone().multiply(-2 * radius)), metersPerParticle)) {
				for (Location point : ParticleUtil.calculateLine(upPoint, upPoint.clone().add(right), metersPerParticle)) {
					if (point.distanceSquared(center) > radiusSq) continue;
					if (cacheFill) cachedFill.add(point.clone().subtract(center).toVector());
					particle.spawnWithCache(cache, point);
				}
			}
		}
	}
	
	private void drawFlatWithCache(LinkedList<Player> cache, Location center, ParticleContainer fill) {
		for (Vector v : cachedEdges) {
			fill.spawnWithCache(cache, center.clone().add(v));
		}
		for (Vector v : cachedFill) {
			fill.spawnWithCache(cache, center.clone().add(v));
		}
	}
}
