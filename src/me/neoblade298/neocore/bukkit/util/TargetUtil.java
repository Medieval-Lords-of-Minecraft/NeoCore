package me.neoblade298.neocore.bukkit.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class TargetUtil {

	public static Location getSightLocation(LivingEntity source, double range, boolean stickToGround) {
		Location start = source.getEyeLocation();
		Vector v = start.getDirection();
		Location end = start.add(v.multiply(range));
		Block b = end.getBlock();
		
		RayTraceResult rtr = start.getWorld().rayTraceBlocks(start, v, range, FluidCollisionMode.NEVER, true);

		/* 1. No stick to ground, hit block: subtract eye direction from the block hit
		 * 2. No stick to ground, air: Do nothing
		 * 3. Stick to ground: Get either the air or raytraced block and find the ground from there
		 */
		if (rtr.getHitBlock() != null) {
			b = rtr.getHitBlock();
			if (!stickToGround) {
				return b.getLocation().subtract(v);
			}
		}
		
		if (stickToGround) {
			end.setY(findGroundY(end.getBlock()));
		}
		return end;
	}
	
	public static LinkedList<LivingEntity> getEntitiesInRadius(Entity source, double range) {
		return getEntitiesInRadius(source.getLocation(), range, range, null);
	}
	
	public static LinkedList<LivingEntity> getEntitiesInRadius(Entity source, double range, Predicate<LivingEntity> filter) {
		return getEntitiesInRadius(source.getLocation(), range, range, filter);
	}
	
	public static LinkedList<LivingEntity> getEntitiesInRadius(Location source, double range) {
		return getEntitiesInRadius(source, range, range, null);
	}
	
	public static LinkedList<LivingEntity> getEntitiesInRadius(Location source, double range, Predicate<LivingEntity> filter) {
		return getEntitiesInRadius(source, range, range, filter);
	}

	// Gets all entities around source
	// Sorted by nearest to furthest
	public static LinkedList<LivingEntity> getEntitiesInRadius(Location source, double radius, double height, Predicate<LivingEntity> filter) {
		Collection<Entity> nearby = source.getNearbyEntities(radius, height, radius);
		TreeSet<DistanceObject<LivingEntity>> sorted = new TreeSet<DistanceObject<LivingEntity>>();

		for (Entity entity : nearby) {
			if (!(entity instanceof LivingEntity)) continue;
			LivingEntity le = (LivingEntity) entity;
			Vector relative = entity.getLocation().subtract(source).toVector();
			sorted.add(new DistanceObject<LivingEntity>(le, relative.lengthSquared()));
		}

		List<LivingEntity> targets = new LinkedList<LivingEntity>();
		for (DistanceObject<LivingEntity> obj : sorted) {
			targets.add(obj.get());
		}
		
		Stream<LivingEntity> stream = sorted.stream().map(obj -> obj.get());
		if (filter != null) stream = stream.filter(filter);
		return stream.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<LivingEntity> getEntitiesInSight(LivingEntity source, double range, double tolerance) {
		return getEntitiesInSight(source, range, tolerance, null);
	}

	// Gets all entities in a line in front of source
	// Sorted by nearest to furthest
	public static LinkedList<LivingEntity> getEntitiesInSight(LivingEntity source, double range, double tolerance, Predicate<LivingEntity> filter) {
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

		Stream<LivingEntity> stream = sorted.stream().map(obj -> obj.get());
		if (filter != null) stream = stream.filter(filter);
		return stream.collect(Collectors.toCollection(LinkedList::new));
	}

	public static LinkedList<LivingEntity> getEntitiesInCone(LivingEntity source, double arc, double range) {
		return getEntitiesInCone(source, arc, range, null);
	}

	public static LinkedList<LivingEntity> getEntitiesInCone(LivingEntity source, double arc, double range, Predicate<LivingEntity> filter) {
		LinkedList<LivingEntity> targets = new LinkedList<LivingEntity>();
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
		if (filter != null) return targets.stream().filter(filter).collect(Collectors.toCollection(LinkedList::new));
		return targets;
	}

	public static boolean isInFront(Entity entity, Entity target) {
		// Get the necessary vectors
		Vector facing = entity.getLocation().getDirection();
		Vector relative = target.getLocation().subtract(entity.getLocation()).toVector();

		// If the dot product is positive, the target is in front
		return facing.dot(relative) >= 0;
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
	
	private static double findGroundY(Block b) {
		while (b.isEmpty()) {
			b = b.getRelative(BlockFace.DOWN);
		}
		return b.getY() + 0.5;
	}
}
