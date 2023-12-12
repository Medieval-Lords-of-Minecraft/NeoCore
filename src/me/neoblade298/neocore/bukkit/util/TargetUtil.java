package me.neoblade298.neocore.bukkit.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class TargetUtil {
	private static final double DEFAULT_TOLERANCE = 4;
	
	public static LivingEntity getFurthestValidEntityInSight(LivingEntity source, double range) {
		List<LivingEntity> list = filter(getEntitiesInSight(source, range, DEFAULT_TOLERANCE), source, false);
		return list.size() > 0 ? list.get(list.size() - 1) : null;
	}
	
	public static LivingEntity getNearestValidEntityInSight(LivingEntity source, double range) {
		List<LivingEntity> list = filter(getEntitiesInSight(source, range, DEFAULT_TOLERANCE), source, false);
		return list.size() > 0 ? list.get(0) : null;
	}
	
	public static List<LivingEntity> getValidEntitiesInSight(LivingEntity source, double range) {
		return filter(getEntitiesInSight(source, range, DEFAULT_TOLERANCE), source, false);
	}
	
	public static List<LivingEntity> getValidEntitiesInSight(LivingEntity source, double range, double tolerance) {
		return filter(getEntitiesInSight(source, range, tolerance), source, false);
	}
	
	public static List<LivingEntity> getValidEntitiesInSight(LivingEntity source, double range, double tolerance, boolean throughWall) {
		return filter(getEntitiesInSight(source, range, tolerance), source, throughWall);
	}
	
	private static List<LivingEntity> filter(List<LivingEntity> list, LivingEntity source, boolean throughWall) {
		Iterator<LivingEntity> iter = list.iterator();
		while (iter.hasNext()) {
			if (!isValidTarget(source, iter.next(), throughWall)) {
				iter.remove();
			}
		}
		return list;
	}

	// Gets all entities in a line in front of source
	// Sorted by nearest to furthest
	public static List<LivingEntity> getEntitiesInSight(LivingEntity source, double range, double tolerance) {
		List<Entity> nearby = source.getNearbyEntities(range, range, range);
		TreeSet<DistanceObject<LivingEntity>> sorted = new TreeSet<DistanceObject<LivingEntity>>();

		Vector facing = source.getLocation().getDirection();
		double fLengthSq = facing.lengthSquared();

		for (Entity entity : nearby) {
			if (!isInFront(source, entity) || !(entity instanceof LivingEntity)) continue;
			LivingEntity le = (LivingEntity) entity;
			Vector relative = entity.getLocation().subtract(source.getLocation()).toVector();
			double dot = relative.dot(facing);
			double rLengthSq = relative.lengthSquared();
			double cosSquared = (dot * dot) / (rLengthSq * fLengthSq);
			double sinSquared = 1 - cosSquared;
			double dSquared = rLengthSq * sinSquared;

			// If close enough to vision line, return the entity
			if (dSquared < tolerance) sorted.add(new DistanceObject<LivingEntity>(le, rLengthSq));
		}

		List<LivingEntity> targets = new LinkedList<LivingEntity>();
		for (DistanceObject<LivingEntity> obj : sorted) {
			targets.add(obj.get());
		}
		return targets;
	}

	public static boolean isInFront(Entity entity, Entity target) {
		// Get the necessary vectors
		Vector facing = entity.getLocation().getDirection();
		Vector relative = target.getLocation().subtract(entity.getLocation()).toVector();

		// If the dot product is positive, the target is in front
		return facing.dot(relative) >= 0;
	}
	
	public static List<LivingEntity> getValidEntitiesInCone(LivingEntity source, double arc, double range) {
		return filter(getEntitiesInCone(source, arc, range), source, false);
	}
	
	public static List<LivingEntity> getValidEntitiesInCone(LivingEntity source, double arc, double range, boolean throughWall) {
		return filter(getEntitiesInCone(source, arc, range), source, throughWall);
	}

	public static List<LivingEntity> getEntitiesInCone(LivingEntity source, double arc, double range) {
		List<LivingEntity> targets = new LinkedList<LivingEntity>();
		List<Entity> list = source.getNearbyEntities(range, range, range);
		if (arc <= 0) return targets;

		// Initialize values
		Vector dir = source.getLocation().getDirection();
		dir.setY(0);
		double cos = Math.cos(arc * Math.PI / 180);
		double cosSq = cos * cos;

		// Get the targets in the cone
		for (Entity entity : list) {
			if (entity instanceof LivingEntity) {

				// Greater than 360 degrees is all targets
				if (arc >= 360) {
					targets.add((LivingEntity) entity);
				}

				// Otherwise, select targets based on dot product
				else {
					Vector relative = entity.getLocation().subtract(source.getLocation()).toVector();
					relative.setY(0);
					double dot = relative.dot(dir);
					double value = dot * dot / relative.lengthSquared();
					if (arc < 180 && dot > 0 && value >= cosSq)
						targets.add((LivingEntity) entity);
					else if (arc >= 180 && (dot > 0 || dot <= cosSq)) targets.add((LivingEntity) entity);
				}
			}
		}

		return targets;
	}

	// Checks if there are blocks between two locations
	public static boolean isObstructed(Location loc1, Location loc2) {
		if (loc1.getWorld() != loc2.getWorld()) return false;
		if (loc1.getX() == loc2.getX() && loc1.getY() == loc2.getY() && loc1.getZ() == loc2.getZ()) {
			return false;
		}

		RayTraceResult rt = loc1.getWorld().rayTraceBlocks(loc1, loc2.toVector().subtract(loc1.toVector()), loc1.distance(loc2));
		return rt.getHitBlock() != null;
	}

	static boolean isValidTarget(final LivingEntity source, final LivingEntity target,
			boolean throughWall) {
		return target != source
				&& (throughWall || !isObstructed(source.getEyeLocation(), target.getEyeLocation()));
	}
	
	private static class DistanceObject<E> implements Comparable<DistanceObject<E>> {
		private double distance;
		private E obj;
		
		public DistanceObject(E obj, double distance) {
			this.distance = distance;
			this.obj = obj;
		}
		
		public E get() {
			return obj;
		}

		@Override
		public int compareTo(DistanceObject<E> d) {
			return Double.compare(this.distance, d.distance);
		}
	}
}
