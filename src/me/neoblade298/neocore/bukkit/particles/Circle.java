package me.neoblade298.neocore.bukkit.particles;

import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Circle extends ParticleShape2D {
	private static final double POINTS_PER_CIRCUMFERENCE = 5;
	private static final double DEFAULT_METERS = 0.5;
	
	private double radius, metersPerParticle;
	private int points;
	private LinkedList<Vector> flatEdges, flatFill; // For flat circles only, can be moved anywhere
	
	public Circle(double radius, int points, double metersPerParticle) {
		this.radius = radius;
		this.points = points;
		this.metersPerParticle = metersPerParticle;
	}
	
	public Circle(double radius, int points) {
		this(radius, points, DEFAULT_METERS);
	}
	
	public Circle(double radius) {
		this(radius, (int) (POINTS_PER_CIRCUMFERENCE * (Math.PI * radius * radius)), DEFAULT_METERS);
	}

	@Override
	public void drawWithCache(LinkedList<Player> cache, ParticleContainer particle, Location center, Vector localRight,
			Vector localUp, Vector localForward, ParticleContainer fill) {
		// If circle is flat, no need to recreate circle except for the first time
		if (localForward.getY() == 1) {
			drawFlatWithCache(cache, particle, center, localRight, localUp, localForward, fill);
		}
		else {
			calculate(center, localRight, localUp, localForward).draw(cache, particle, fill);
		}
	}
	
	private void drawFlatWithCache(LinkedList<Player> cache, ParticleContainer particle, Location center, Vector localRight, Vector localUp, Vector localForward, ParticleContainer fill) {
		if (flatEdges == null) {
			ParticleShapeMemory mem = calculate(center, localRight, localUp, localForward);
			flatEdges = mem.getEdgeVectors();
			flatFill = mem.getFillVectors();
		}
		for (Vector v : flatEdges) {
			particle.spawnWithCache(cache, center.clone().add(v));
		}
		if (fill == null) return;
		for (Vector v : flatFill) {
			fill.spawnWithCache(cache, center.clone().add(v));
		}
	}

	@Override
	public ParticleShapeMemory calculate(Location center, Vector localRight, Vector localUp, Vector localForward) {
		double rotationPerPoint = (2 * Math.PI) / (double) points;
		Vector rotator = localUp.clone().multiply(radius);
		
		LinkedList<Location> edges = new LinkedList<Location>();
		for (int i = 0; i < points; i++) {
			edges.add(center.clone().add(rotator.rotateAroundAxis(localForward, rotationPerPoint)));
		}

		LinkedList<Location> fill = new LinkedList<Location>();
		Location topLeft = center.add(localRight.clone().multiply(radius)).add(localUp.clone().multiply(radius));
		Vector right = localUp.clone().multiply(radius * 2);
		double radiusSq = radius * radius;
		for (Location upPoint : ParticleUtil.calculateLine(topLeft, topLeft.clone().add(localUp.clone().multiply(-2 * radius)), metersPerParticle)) {
			for (Location point : ParticleUtil.calculateLine(upPoint, upPoint.clone().add(right), metersPerParticle)) {
				if (point.distanceSquared(center) > radiusSq) continue;
				fill.add(point);
			}
		}
		
		return new ParticleShapeMemory(center, edges, fill);
	}
}
