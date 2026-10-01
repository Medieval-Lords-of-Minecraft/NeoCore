package me.neoblade298.neocore.bukkit.effects;

import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class LocalAxes {
	private Vector left, up, forward;
	private static final LocalAxes XZ = new LocalAxes(new Vector(-1,0,0), new Vector(0,0,1), new Vector(0,1,0));
	
	public LocalAxes(Vector left, Vector up, Vector forward) {
		this.left = normalize(left, "Left axis");
		this.up = normalize(up, "Up axis");
		this.forward = normalize(forward, "Forward axis");
	}
	
	public Vector left() {
		return left.clone();
	}
	
	public Vector up() {
		return up.clone();
	}
	
	public Vector forward() {
		return forward.clone();
	}
	
	public boolean isXZ() {
		return this.left.getX() == -1 && this.up.getZ() == 1;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof LocalAxes other)) return false;
		return left.equals(other.left) && up.equals(other.up) && forward.equals(other.forward);
	}

	@Override
	public int hashCode() {
		return Objects.hash(left, up, forward);
	}
	
	public static LocalAxes xz() {
		return XZ;
	}
	
	// Gets the axes of the player's eye direction
	public static LocalAxes usingEyeLocation(LivingEntity e) {
		Location eyeLocation = e.getEyeLocation();
		Vector localUp = findLocalUp(eyeLocation);
		Vector localRight = eyeLocation.getDirection().clone().crossProduct(localUp);
		return new LocalAxes(localRight.multiply(-1), localUp, eyeLocation.getDirection());
	}
	
	// Gets the axes of the player's eye direction, but with xz axis flat with global y axis
	public static LocalAxes usingGroundedEyeLocation(LivingEntity e) {
		Location horizontal = e.getEyeLocation();
		horizontal.setPitch(0);
		Vector localForward = horizontal.getDirection();
		Vector localUp = new Vector(0,1,0);
		Vector localLeft = localForward.clone().rotateAroundY(-Math.PI / 2);
		return new LocalAxes(localLeft, localUp, localForward);
	}
	
	// Rotates the pitch 90 degrees upwards (apparently up is negative)
	private static Vector findLocalUp(Location loc) {
		float pitch = loc.getPitch();
		float yaw = loc.getYaw();
		pitch -= 90;
		if (pitch < -90) {
			pitch = -90 + (Math.abs(pitch) - 90);
			yaw = ((yaw + 180) % 360);
		}
		Location yv = loc.clone();
		yv.setPitch(pitch);
		yv.setYaw(yaw);
		return yv.getDirection();
	}

	private static Vector normalize(Vector vector, String name) {
		if (vector == null
				|| !Double.isFinite(vector.getX())
				|| !Double.isFinite(vector.getY())
				|| !Double.isFinite(vector.getZ())
				|| vector.lengthSquared() == 0) {
			throw new IllegalArgumentException(name + " must be finite and non-zero");
		}
		return vector.clone().normalize();
	}
}
