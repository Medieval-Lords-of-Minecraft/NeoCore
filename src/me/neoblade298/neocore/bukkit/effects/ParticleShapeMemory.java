package me.neoblade298.neocore.bukkit.effects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ParticleShapeMemory {
	private final Location center;
	private final List<Location> edges, fill;
	
	public ParticleShapeMemory(Location center, LinkedList<Location> edges, LinkedList<Location> fill) {
		this(center, (Collection<Location>) edges, fill);
	}

	public ParticleShapeMemory(Location center, Collection<Location> edges, Collection<Location> fill) {
		ParticleUtil.ensurePointBudget(edges.size(), fill.size());
		this.center = center.clone();
		this.edges = cloneLocations(edges);
		this.fill = cloneLocations(fill);
	}
	
	public void play(ParticleContainer edge) {
		play(edge, null);
	}
	
	public void play(ParticleContainer edge, ParticleContainer fill) {
		for (Location loc : edges) {
			edge.play(loc);
		}
		if (fill != null) {
			for (Location loc : this.fill) {
				fill.play(loc);
			}
		}
	}

	public void playGlobal(ParticleContainer edge) {
		play(edge);
	}

	public void playGlobal(ParticleContainer edge, ParticleContainer fill) {
		play(edge, fill);
	}
	
	public void play(Player origin, ParticleContainer edge) {
		playWithCache(Effect.calculateCache(origin, center, edge.getForcedVisibility(), ParticleContainer.HIDE_TAG), edge);
	}
	
	public void play(Player origin, ParticleContainer edge, ParticleContainer fill) {
		playWithCache(Effect.calculateCache(origin, center, edge.getForcedVisibility(), ParticleContainer.HIDE_TAG), edge, fill);
	}

	public void playFromPlayer(Player origin, ParticleContainer edge) {
		play(origin, edge);
	}

	public void playFromPlayer(Player origin, ParticleContainer edge, ParticleContainer fill) {
		play(origin, edge, fill);
	}
	
	public void playWithCache(List<Player> cache, ParticleContainer edge) {
		playWithCache(cache, edge, null);
	}
	
	public void playWithCache(List<Player> cache, ParticleContainer edge, ParticleContainer fill) {
		for (Location loc : edges) {
			edge.playWithCache(cache, loc);
		}
		if (fill != null) {
			for (Location loc : this.fill) {
				fill.playWithCache(cache, loc);
			}
		}
	}

	public void playAtWithCache(List<Player> cache, ParticleContainer edge, Location newCenter, ParticleContainer fill) {
		for (Location location : edges) {
			edge.playWithCache(cache, translate(location, newCenter));
		}
		if (fill != null) {
			for (Location location : this.fill) {
				fill.playWithCache(cache, translate(location, newCenter));
			}
		}
	}

	public LinkedList<Location> getEdges() {
		return new LinkedList<Location>(cloneLocations(edges));
	}

	public LinkedList<Location> getFill() {
		return new LinkedList<Location>(cloneLocations(fill));
	}
	
	public LinkedList<Vector> getEdgeVectors() {
		LinkedList<Vector> evs = new LinkedList<Vector>();
		for (Location loc : edges) {
			evs.add(loc.clone().subtract(center).toVector());
		}
		return evs;
	}
	
	public LinkedList<Vector> getFillVectors() {
		LinkedList<Vector> fvs = new LinkedList<Vector>();
		for (Location loc : fill) {
			fvs.add(loc.clone().subtract(center).toVector());
		}
		return fvs;
	}

	private static List<Location> cloneLocations(Collection<Location> locations) {
		List<Location> copy = new ArrayList<Location>(locations.size());
		for (Location location : locations) {
			copy.add(location.clone());
		}
		return copy;
	}

	private Location translate(Location location, Location newCenter) {
		return newCenter.clone().add(
				location.getX() - center.getX(),
				location.getY() - center.getY(),
				location.getZ() - center.getZ());
	}
}
