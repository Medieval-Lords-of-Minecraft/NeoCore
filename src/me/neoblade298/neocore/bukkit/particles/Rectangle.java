package me.neoblade298.neocore.bukkit.particles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.neoblade298.neocore.bukkit.particles.ParticleShape;

public class Rectangle extends ParticleShape {
	private static final double PARTICLES_PER_METER = 2;
	
	private Location bottomLeft;
	private int heightIterations;
	private double blocksPerParticle;
	private Vector vWidth, vHeight;
	

	public Rectangle(Player p, double width, double height, double distanceFromPlayer) {
		this(p, width, height, distanceFromPlayer, 1 / PARTICLES_PER_METER);
	}
	
	// Create a rectangle where a player is facing but stuck to the ground, primarily for shield walls
	public Rectangle(Player p, double width, double height, double distanceFromPlayer, double blocksPerParticle) {
		this.heightIterations = (int) (height / blocksPerParticle);
		this.blocksPerParticle = blocksPerParticle;
		
		Vector localForward = p.getEyeLocation().getDirection().normalize();
		Vector localLeft = localForward.clone().setY(0).rotateAroundY(Math.PI / 2);
		Vector localUp = localForward.clone().rotateAroundAxis(localLeft, Math.PI / 2);
		
		vWidth = localLeft.clone().multiply(width / 2);
		vHeight = localUp.multiply(height / blocksPerParticle);
		bottomLeft = p.getLocation().add(localForward.multiply(distanceFromPlayer));
		bottomLeft.add(vWidth);
		vWidth.multiply(-2);
	}
	
	@Override
	public void draw(Player p, boolean showAllPlayers, Particle part, DustOptions options) {
		Location left = bottomLeft.clone();
		for (int i = 0; i < heightIterations; i++) {
			Location right = left.clone().add(vWidth);
			ParticleUtil.drawLine(p, left, right, part, showAllPlayers, 1, 0, 0, blocksPerParticle, options);
			left.add(vHeight);
		}
	}
}
