package me.neoblade298.neocore.bukkit.effects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Rectangle extends ParticleShape2D {
	private static final double DEFAULT_METERS = 0.5;
	private double length, height, metersPerParticle;
	private LocalAxes cachedAxes;
	private ParticleShapeMemory cachedShape;
	
	public Rectangle(double length, double height) {
		this(length, height, DEFAULT_METERS);
	}
	
	public Rectangle(double length, double height, double metersPerParticle) {
		if (!Double.isFinite(length) || length < 0 || !Double.isFinite(height) || height < 0) {
			throw new IllegalArgumentException("Rectangle dimensions must be finite and non-negative");
		}
		ParticleUtil.validateSpacing(metersPerParticle);
		this.length = length;
		this.height = height;
		this.metersPerParticle = metersPerParticle;
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
		Location bl = center.clone().add(axes.left().multiply(length * 0.5).add(axes.up().multiply(height * -0.5)));
		Location tl = bl.clone().add(axes.up().multiply(height));
		Vector right = axes.left().multiply(-length);
		Location br = bl.clone().add(right);
		Location tr = tl.clone().add(right);
		List<Location> edges = new ArrayList<Location>(
				ParticleUtil.calculateLinePoints(tl, bl, metersPerParticle, true));
		List<Location> leftEdge = new ArrayList<Location>(edges);
		addEdgeWithinBudget(edges, ParticleUtil.calculateLinePoints(br, tr, metersPerParticle, true));
		addEdgeWithinBudget(edges, ParticleUtil.calculateLinePoints(tr, tl, metersPerParticle, false));
		addEdgeWithinBudget(edges, ParticleUtil.calculateLinePoints(bl, br, metersPerParticle, false));

		List<Location> fill = new ArrayList<Location>();
		for (Location upPoint : leftEdge) {
			List<Location> row = ParticleUtil.calculateLinePoints(upPoint, upPoint.clone().add(right), metersPerParticle, true);
			ParticleUtil.ensurePointBudget(edges.size(), fill.size() + row.size());
			fill.addAll(row);
		}
		return new ParticleShapeMemory(center, edges, fill);
	}

	private static void addEdgeWithinBudget(List<Location> edges, List<Location> additionalEdges) {
		ParticleUtil.ensurePointBudget(edges.size() + additionalEdges.size());
		edges.addAll(additionalEdges);
	}
}
