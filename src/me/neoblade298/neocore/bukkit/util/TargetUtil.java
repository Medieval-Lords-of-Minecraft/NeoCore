package me.neoblade298.neocore.bukkit.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class TargetUtil {

	// Gets all entities in a line in front of source
	public static List<LivingEntity> getEntitiesInLineOfSight(LivingEntity source, double range, double tolerance) {
		List<Entity> list = source.getNearbyEntities(range, range, range);
		List<LivingEntity> targets = new ArrayList<LivingEntity>();

		Vector facing = source.getLocation().getDirection();
		double fLengthSq = facing.lengthSquared();

		for (Entity entity : list) {
			if (!isInFront(source, entity) || !(entity instanceof LivingEntity)) continue;

			Vector relative = entity.getLocation().subtract(source.getLocation()).toVector();
			double dot = relative.dot(facing);
			double rLengthSq = relative.lengthSquared();
			double cosSquared = (dot * dot) / (rLengthSq * fLengthSq);
			double sinSquared = 1 - cosSquared;
			double dSquared = rLengthSq * sinSquared;

			// If close enough to vision line, return the entity
			if (dSquared < tolerance) targets.add((LivingEntity) entity);
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
		List<LivingEntity> targets = new ArrayList<LivingEntity>();
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
}
