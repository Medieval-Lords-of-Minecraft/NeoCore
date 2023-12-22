package me.neoblade298.neocore.bukkit.particles;

import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ParticleShapeMemory {
	private Location center;
	private LinkedList<Location> edges, fill;
	
	public ParticleShapeMemory(Location center, LinkedList<Location> edges, LinkedList<Location> fill) {
		this.center = center;
		this.edges = edges;
		this.fill = fill;
	}
	
	public LinkedList<Player> calculateCache() {
		return ParticleUtil.calculateCache(center);
	}
	
	public void draw(LinkedList<Player> cache, ParticleContainer edge) {
		edge.spawn(edges);
	}
	
	public void draw(LinkedList<Player> cache, ParticleContainer edge, ParticleContainer fill) {
		edge.spawn(edges);
		if (fill != null) fill.spawn(this.fill);
	}

	public LinkedList<Location> getEdges() {
		return edges;
	}

	public LinkedList<Location> getFill() {
		return fill;
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
}
