package me.neoblade298.neocore.bukkit.util;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class TargetUtil {

	// Gets all entities around source
	// Sorted by nearest to furthest
	public static List<LivingEntity> getEntitiesInRadius(LivingEntity source, double radius, double tolerance) {
		List<Entity> nearby = source.getNearbyEntities(radius, radius, radius);
		TreeSet<DistanceObject<LivingEntity>> sorted = new TreeSet<DistanceObject<LivingEntity>>();

		for (Entity entity : nearby) {
			LivingEntity le = (LivingEntity) entity;
			Vector relative = entity.getLocation().subtract(source.getLocation()).toVector();
			sorted.add(new DistanceObject<LivingEntity>(le, relative.lengthSquared()));
		}

		List<LivingEntity> targets = new LinkedList<LivingEntity>();
		for (DistanceObject<LivingEntity> obj : sorted) {
			targets.add(obj.get());
		}
		return targets;
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
