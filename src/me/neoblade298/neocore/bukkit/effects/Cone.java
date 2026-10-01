package me.neoblade298.neocore.bukkit.effects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Cone extends ParticleShape2D {
	private static final double POINTS_PER_CIRCUMFERENCE = 1;
	private static final double DEFAULT_METERS = 0.5;
	
	private double length, degrees, metersPerParticle;
	private int lines;
	private LocalAxes cachedAxes;
	private ParticleShapeMemory cachedShape;
	
	public Cone(double length, double degrees, int lines, double metersPerParticle) {
		if (!Double.isFinite(length) || length < 0) {
			throw new IllegalArgumentException("Length must be finite and non-negative");
		}
		if (!Double.isFinite(degrees) || degrees < 0 || degrees > 360) {
			throw new IllegalArgumentException("Degrees must be finite and between 0 and 360");
		}
		if (lines <= 0) {
			throw new IllegalArgumentException("Lines must be greater than zero");
		}
		ParticleUtil.validateSpacing(metersPerParticle);
		ParticleUtil.ensurePointBudget((long) lines + 1);
		this.length = length;
		this.degrees = degrees;
		this.lines = lines;
		this.metersPerParticle = metersPerParticle;
	}
	
	public Cone(double length, double degrees, int lines) {
		this(length, degrees, lines, DEFAULT_METERS);
	}
	
	public Cone(double length, double degrees) {
		this(length, degrees, Math.max(1, (int) Math.ceil(POINTS_PER_CIRCUMFERENCE * length * Math.toRadians(degrees))), DEFAULT_METERS);
	}

	@Override
	public void playWithCache(List<Player> cache, ParticleContainer particle, Location center, LocalAxes axes, ParticleContainer fill) {
		if (cachedAxes != null && cachedAxes.equals(axes)) {
			cachedShape.playAtWithCache(cache, particle, center, fill);
			return;
		}

		cachedAxes = axes;
		cachedShape = calculate(center, axes);
		cachedShape.playWithCache(cache, particle, fill);
	}

	@Override
	public ParticleShapeMemory calculate(Location center, LocalAxes axes) {
		Vector line = axes.forward().multiply(length).rotateAroundAxis(axes.up(), Math.toRadians(-degrees / 2));
		List<Location> edges = new ArrayList<Location>(
				ParticleUtil.calculateLinePoints(center, center.clone().add(line), metersPerParticle, false));
		List<Location> fill = new ArrayList<Location>();
		double rotationPerLine = Math.toRadians(degrees / lines);
		for (int i = 1; i < lines; i++) {
			line.rotateAroundAxis(axes.up(), rotationPerLine);
			Location edge = center.clone().add(line);
			edges.add(edge);
			List<Location> lineFill = ParticleUtil.calculateLinePoints(center, edge, metersPerParticle, true);
			ParticleUtil.ensurePointBudget(edges.size(), fill.size() + lineFill.size());
			fill.addAll(lineFill);
		}
		line.rotateAroundAxis(axes.up(), rotationPerLine);
		Location edge = center.clone().add(line);
		edges.add(edge);
		List<Location> finalEdge = ParticleUtil.calculateLinePoints(center, edge, metersPerParticle, true);
		ParticleUtil.ensurePointBudget(edges.size() + finalEdge.size(), fill.size());
		edges.addAll(finalEdge);
		return new ParticleShapeMemory(center, edges, fill);
	}
}
