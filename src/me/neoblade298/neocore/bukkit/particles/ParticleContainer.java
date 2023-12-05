package me.neoblade298.neocore.bukkit.particles;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

public class ParticleContainer {
	protected Player origin;
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
		this.dustOptions = null;
		this.blockData = blockData;
		return this;
	}
	
	public ParticleContainer setDustOptions(DustOptions dustOptions) {
		this.blockData = null;
		this.dustOptions = dustOptions;
		return this;
	}
	
	public ParticleContainer setOrigin(Player origin) {
		this.origin = origin;
		return this;
	}
	
	public void forceSpawn(Player p) {
		forceSpawn(p.getLocation());
	}
	
	// Ignore player settings
	public void forceSpawn(Location loc) {
		loc.getWorld().spawnParticle(particle, loc, count, offsetXZ, offsetY, offsetXZ, speed, blockData != null ? blockData : dustOptions);
	}
	
	public void spawn(Location loc) {
		for (Player p : ParticleUtil.calculateCache(loc)) {
			p.spawnParticle(particle, loc, count, offsetXZ, offsetY, offsetXZ, speed, blockData != null ? blockData : dustOptions);
		}
	}
	
	public void spawn(Player loc) {
		spawn(loc.getLocation());
	}
	
	// Skips calculating the players to target, useful for particle shapes
	public void spawnWithCache(ArrayList<Player> cache, Location loc) {
		for (Player p : cache) {
			p.spawnParticle(particle, loc, count, offsetXZ, offsetY, offsetXZ, speed, blockData != null ? blockData : dustOptions);
		}
	}
}
