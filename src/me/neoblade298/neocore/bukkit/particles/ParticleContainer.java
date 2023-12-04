package me.neoblade298.neocore.bukkit.particles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

public class ParticleContainer {
	protected Particle particle;
	protected int count;
	protected double offsetXZ, offsetY, speed;
	protected BlockData blockData;
	protected DustOptions dustOptions;
	
	public ParticleContainer(Particle particle) {
		this.particle = particle;
	}
	
	public ParticleContainer clone() {
		ParticleContainer pc = new ParticleContainer(particle);
		pc.setCount(count);
		pc.setOffset(offsetXZ, offsetY);
		pc.setSpeed(speed);
		pc.setBlockData(blockData);
		pc.setDustOptions(dustOptions);
		return pc;
	}
	
	public ParticleContainer setCount(int count) {
		this.count = count;
		return this;
	}
	
	public ParticleContainer setOffset(double offsetXZ, double offsetY) {
		this.offsetXZ = offsetXZ;
		this.offsetY = offsetY;
		return this;
	}
	
	public ParticleContainer setSpeed(double speed) {
		this.speed = speed;
		return this;
	}
	
	public ParticleContainer setBlockData(BlockData blockData) {
		this.blockData = blockData;
		return this;
	}
	
	public ParticleContainer setDustOptions(DustOptions dustOptions) {
		this.dustOptions = dustOptions;
		return this;
	}
	
	public void spawn(Player p) {
		spawn(p.getLocation());
	}
	
	public PrivateParticleContainer setAudience(Player audience) {
		return new PrivateParticleContainer(this, audience);
	}
	
	public void spawn(Location loc) {
		loc.getWorld().spawnParticle(particle, loc, count, offsetXZ, offsetY, offsetXZ, speed, blockData != null ? blockData : dustOptions);
	}
}
