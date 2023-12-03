package me.neoblade298.neocore.bukkit.particles;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class Rectangle extends ParticleShape {
	private static final double PARTICLES_PER_METER = 2;
	
	private Location bottomLeft;
	private int heightIterations;
	private double blocksPerParticle;
	private Vector vIterWidth, vIterHeight, vWidth, vHeight;
	
	public Rectangle(LivingEntity owner, double width, double height, double forward, double forwardOffset) {
		this(owner, width, height, forward, forwardOffset, 1 / PARTICLES_PER_METER);
	}
	
	// Create a rectangle where an entity is facing but stuck to the ground, primarily for shield walls
	public Rectangle(LivingEntity owner, double width, double height, double forward, double forwardOffset, double blocksPerParticle) {
		this.heightIterations = (int) (height / blocksPerParticle);
		this.blocksPerParticle = blocksPerParticle;
		
		Vector localForward = owner.getEyeLocation().getDirection().normalize();
		Vector localLeft = localForward.clone().setY(0).rotateAroundY(Math.PI / 2);
		Vector localUp = localForward.clone().rotateAroundAxis(localLeft, -Math.PI / 2);
		
		vWidth = localLeft.clone().multiply(-width);
		vHeight = localUp.clone().multiply(height);

		vIterWidth = localLeft.clone().multiply(width / 2);
		vIterHeight = localUp.multiply(blocksPerParticle);
		bottomLeft = owner.getLocation().add(localForward.clone().multiply(forwardOffset));
		bottomLeft.add(localForward.clone().multiply(forward));
		bottomLeft.add(vIterWidth);
		vIterWidth.multiply(-2);
	}
	
	@Override
	public void draw(ParticleContainer container) {
		Location left = bottomLeft.clone();
		for (int i = 0; i < heightIterations; i++) {
			Location right = left.clone().add(vIterWidth);
			ParticleUtil.drawLine(container, left, right, blocksPerParticle);
			left.add(vIterHeight);
		}
	}
	
	@Override
	public void drawEdges(ParticleContainer container) {
		Location bl = bottomLeft.clone();
		Location br = bl.clone().add(vWidth);
		Location tl = bl.clone().add(vHeight);
		Location tr = tl.clone().add(vWidth);
		ParticleUtil.drawLine(container, bl, br, blocksPerParticle);
		ParticleUtil.drawLine(container, br, tr, blocksPerParticle);
		ParticleUtil.drawLine(container, tr, tl, blocksPerParticle);
		ParticleUtil.drawLine(container, tl, bl, blocksPerParticle);
	}
}
