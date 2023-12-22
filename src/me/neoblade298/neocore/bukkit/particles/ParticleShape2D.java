package me.neoblade298.neocore.bukkit.particles;


import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

// By default, draws the shape flat on the ground
public abstract class ParticleShape2D {
	// Vertical and horizontal axis are normalized vectors, for a flat draw just use x and z vector
	public abstract void drawWithCache(LinkedList<Player> cache, ParticleContainer edges, Location center, Vector localRight, Vector localUp, Vector localForward, ParticleContainer fill);
	
	public abstract ParticleShapeMemory calculate(Location center, Vector localRight, Vector localUp, Vector localForward);
	
	public void draw(ParticleContainer edges, Location center, Vector localRight, Vector localUp, Vector localForward, ParticleContainer fill) {
		drawWithCache(ParticleUtil.calculateCache(center), edges, center, localRight, localUp, localForward, fill);
	}
	public void draw(ParticleContainer edges, Location center) {
		draw(edges, center, new Vector(1,0,0), new Vector(0,0,1), new Vector(0,1,0), null);
	}
	public void drawFilled(ParticleContainer edges, Location center, ParticleContainer fill) {
		draw(edges, center, new Vector(1,0,0), new Vector(0,0,1), new Vector(0,1,0), fill);
	}
	public void drawWithEyeLocation(ParticleContainer edges, Location eyeLocation) {
		Vector localUp = findLocalUp(eyeLocation);
		Vector localRight = eyeLocation.getDirection().crossProduct(localUp);
		draw(edges, eyeLocation, localRight, localUp, eyeLocation.getDirection(), null);
	}
	public void drawFilledWithEyeLocation(ParticleContainer edges, Location eyeLocation, ParticleContainer fill) {
		Vector localUp = findLocalUp(eyeLocation);
		Vector localRight = eyeLocation.getDirection().crossProduct(localUp);
		draw(edges, eyeLocation, localRight, localUp, eyeLocation.getDirection(), fill);
	}
	
	// Rotates the pitch 90 degrees upwards (apparently up is negative)
	public static Vector findLocalUp(Location loc) {
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
}
