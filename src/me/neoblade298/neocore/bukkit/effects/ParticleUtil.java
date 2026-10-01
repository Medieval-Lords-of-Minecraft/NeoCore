package me.neoblade298.neocore.bukkit.effects;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ParticleUtil {
	public static final int MAX_GENERATED_POINTS = 10_000;

	public static List<Player> drawLine(Player origin, ParticleContainer particle, Location l1, Location l2, double metersPerParticle) {
		return drawLineWithCache(particle.calculateCache(origin, l1, particle.forceVisibility), particle, l1, l2, metersPerParticle);
	}
	
	public static LinkedList<Location> calculateLine(Location l1, Location l2, double metersPerParticle) {
		return calculateLine(l1, l2, metersPerParticle, false);
	}
	
	public static LinkedList<Location> calculateLine(Location l1, Location l2, double metersPerParticle, boolean removeEdges) {
		return new LinkedList<Location>(calculateLinePoints(l1, l2, metersPerParticle, removeEdges));
	}

	static List<Location> calculateLinePoints(Location l1, Location l2, double metersPerParticle, boolean removeEdges) {
		validateSpacing(metersPerParticle);
		if (l1.getWorld() != l2.getWorld()) {
			throw new IllegalArgumentException("Particle line locations must be in the same world");
		}

		Location start = l1.clone();
		Vector difference = l2.toVector().subtract(l1.toVector());
		double distance = difference.length();
		if (distance == 0) {
			List<Location> locations = new ArrayList<Location>(removeEdges ? 0 : 1);
			if (!removeEdges) locations.add(start);
			return locations;
		}

		long segmentCount = (long) Math.ceil(distance / metersPerParticle);
		long pointCount = removeEdges ? Math.max(0, segmentCount - 1) : segmentCount + 1;
		ensurePointBudget(pointCount);

		int segments = (int) segmentCount;
		int points = removeEdges ? Math.max(0, segments - 1) : segments + 1;
		List<Location> locations = new ArrayList<Location>(points);
		Vector step = difference.multiply(1D / segments);
		int first = removeEdges ? 1 : 0;
		int last = removeEdges ? segments - 1 : segments;
		for (int i = first; i <= last; i++) {
			locations.add(l1.clone().add(step.clone().multiply(i)));
		}
		return locations;
	}
	
	public static List<Player> drawLineWithCache(List<Player> cache, ParticleContainer particle, Location l1, Location l2, double metersPerParticle) {
		for (Location loc : calculateLinePoints(l1, l2, metersPerParticle, false)) {
			particle.playWithCache(cache, loc);
		}
		return cache;
	}

	static void validateSpacing(double metersPerParticle) {
		if (!Double.isFinite(metersPerParticle) || metersPerParticle <= 0) {
			throw new IllegalArgumentException("Meters per particle must be finite and greater than zero");
		}
	}

	static void ensurePointBudget(long points) {
		if (points > MAX_GENERATED_POINTS) {
			throw new IllegalArgumentException("Particle geometry exceeds the " + MAX_GENERATED_POINTS + "-point limit");
		}
	}

	static void ensurePointBudget(int edges, int fill) {
		long points = (long) edges + fill;
		if (points > MAX_GENERATED_POINTS) {
			throw new IllegalArgumentException("Particle geometry exceeds the " + MAX_GENERATED_POINTS + "-point limit");
		}
	}
}
