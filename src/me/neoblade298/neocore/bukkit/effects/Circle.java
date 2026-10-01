package me.neoblade298.neocore.bukkit.effects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Circle extends ParticleShape2D {
	private static final double POINTS_PER_CIRCUMFERENCE = 1;
	private static final double DEFAULT_METERS = 0.5;
	
	private double radius, metersPerParticle;
	private int points;
	private List<Vector> flatEdges, flatFill; // For flat circles only, can be moved anywhere
	private LocalAxes cachedAxes;
	private ParticleShapeMemory cachedShape;
	
	public Circle(double radius, int points, double metersPerParticle) {
		if (!Double.isFinite(radius) || radius < 0) {
			throw new IllegalArgumentException("Radius must be finite and non-negative");
		}
		if (points <= 0) {
			throw new IllegalArgumentException("Points must be greater than zero");
		}
		ParticleUtil.validateSpacing(metersPerParticle);
		ParticleUtil.ensurePointBudget(points);
		this.radius = radius;
		this.points = points;
		this.metersPerParticle = metersPerParticle;
	}
	
	public Circle(double radius, int points) {
		this(radius, points, DEFAULT_METERS);
	}
	
	public Circle(double radius) {
		this(radius, Math.max(1, (int) Math.ceil(POINTS_PER_CIRCUMFERENCE * 2 * Math.PI * radius)), DEFAULT_METERS);
	}

	@Override
	public void playWithCache(List<Player> cache, ParticleContainer particle, Location center, LocalAxes axes, ParticleContainer fill) {
		// If circle is flat, no need to recreate circle except for the first time
		if (axes.isXZ()) {
			drawFlatWithCache(cache, particle, center, fill);
		}
		else {
			drawOrientedWithCache(cache, particle, center, axes, fill);
		}
	}

	private void drawOrientedWithCache(List<Player> cache, ParticleContainer particle, Location center, LocalAxes axes, ParticleContainer fill) {
		if (cachedAxes != null && cachedAxes.equals(axes)) {
			cachedShape.playAtWithCache(cache, particle, center, fill);
			return;
		}

		cachedAxes = axes;
		cachedShape = calculate(center, axes);
		cachedShape.playWithCache(cache, particle, fill);
	}
	
	private void drawFlatWithCache(List<Player> cache, ParticleContainer particle, Location center, ParticleContainer fill) {
		LocalAxes axes = LocalAxes.xz();
		if (flatEdges == null) {
			ParticleShapeMemory mem = calculate(center, axes);
			flatEdges = new ArrayList<Vector>(mem.getEdgeVectors());
			flatFill = new ArrayList<Vector>(mem.getFillVectors());
			mem.playWithCache(cache, particle, fill);
			return;
		}
		
		for (Vector v : flatEdges) {
			particle.playWithCache(cache, center.clone().add(v));
		}
		if (fill == null) return;
		for (Vector v : flatFill) {
			fill.playWithCache(cache, center.clone().add(v));
		}
	}

	@Override
	public ParticleShapeMemory calculate(Location center, LocalAxes axes) {
		double rotationPerPoint = (2 * Math.PI) / (double) points;
		Vector rotator = axes.up().multiply(radius);
		
		List<Location> edges = new ArrayList<Location>(points);
		for (int i = 0; i < points; i++) {
			edges.add(center.clone().add(rotator.rotateAroundAxis(axes.forward(), rotationPerPoint)));
		}

		List<Location> fill = new ArrayList<Location>();
		Location topLeft = center.clone().add(axes.left().multiply(radius)).add(axes.up().multiply(radius));
		Vector right = axes.left().multiply(radius * -2);
		Vector down = axes.up().multiply(radius * -2);
		double radiusSq = radius * radius;
		for (Location horizontal : ParticleUtil.calculateLinePoints(topLeft, topLeft.clone().add(right), metersPerParticle, true)) {
			for (Location point : ParticleUtil.calculateLinePoints(horizontal, horizontal.clone().add(down), metersPerParticle, true)) {
				
				if (point.distanceSquared(center) >= radiusSq) continue;
				fill.add(point);
				ParticleUtil.ensurePointBudget(edges.size(), fill.size());
			}
		}
		
		return new ParticleShapeMemory(center, edges, fill);
	}
}
