package me.neoblade298.neocore.bukkit.particles;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ParticleContainer {
	protected Player origin;
	protected Particle particle;
	protected int count = 1;
	protected double spreadXZ, spreadY, speed, offsetForward, offsetForwardAngle, offsetY;
	protected BlockData blockData;
	protected DustOptions dustOptions;
	protected boolean ignoreSettings;
	
	
	public ParticleContainer(Particle particle) {
		this.particle = particle;
		
		if (particle == Particle.REDSTONE) dustOptions = new DustOptions(Color.RED, count);
	}
	
	public ParticleContainer clone() {
		ParticleContainer pc = new ParticleContainer(particle);
		pc.count(count);
		pc.spread(spreadXZ, spreadY);
		pc.speed(speed);
		pc.blockData(blockData);
		pc.dustOptions(dustOptions);
		return pc;
	}
	
	public ParticleContainer count(int count) {
		this.count = count;
		return this;
	}
	
	public ParticleContainer ignoreSettings(boolean ignoreSettings) {
		this.ignoreSettings = ignoreSettings;
		return this;
	}
	
	public ParticleContainer spread(double spreadXZ, double spreadY) {
		this.spreadXZ = spreadXZ;
		this.spreadY = spreadY;
		return this;
	}
	
	public ParticleContainer offsetY(double offsetY) {
		this.offsetY = offsetY;
		return this;
	}
	
	public ParticleContainer speed(double speed) {
		this.speed = speed;
		return this;
	}
	
	public ParticleContainer blockData(BlockData blockData) {
		this.dustOptions = null;
		this.blockData = blockData;
		return this;
	}
	
	public ParticleContainer dustOptions(DustOptions dustOptions) {
		this.blockData = null;
		this.dustOptions = dustOptions;
		return this;
	}
	
	public ParticleContainer origin(Player origin) {
		this.origin = origin;
		return this;
	}
	
	public void spawn(Location loc) {
		loc.add(0, offsetY, 0);
		if (ignoreSettings) {
			loc.getWorld().spawnParticle(particle, loc, count, spreadXZ, spreadY, spreadXZ, speed, blockData != null ? blockData : dustOptions);
		}
		else {
			for (Player p : ParticleUtil.calculateCache(loc)) {
				p.spawnParticle(particle, loc, count, spreadXZ, spreadY, spreadXZ, speed, blockData != null ? blockData : dustOptions);
			}
		}
	}
	
	public void spawn(Entity loc) {
		spawn(loc.getLocation());
	}
	
	// Skips calculating the players to target, useful for particle shapes
	public void spawnWithCache(ArrayList<Player> cache, Location loc) {
		for (Player p : cache) {
			p.spawnParticle(particle, loc, count, spreadXZ, spreadY, spreadXZ, speed, blockData != null ? blockData : dustOptions);
		}
	}
}
