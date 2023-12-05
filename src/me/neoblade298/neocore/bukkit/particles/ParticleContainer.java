package me.neoblade298.neocore.bukkit.particles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.player.PlayerTags;

public class ParticleContainer {
	protected Player origin;
	protected Particle particle;
	protected int count;
	protected double offsetXZ, offsetY, speed;
	protected BlockData blockData;
	protected DustOptions dustOptions;
	
	private static final int MAX_VIEW_DISTANCE = 1024;
	private static final PlayerTags tags;
	
	static {
		tags = NeoCore.getNeoCoreTags();
	}
	
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
	
	public void spawn(Location loc) {
		for (Player p : loc.getWorld().getPlayers()) {
			if (loc.distanceSquared(p.getLocation()) > MAX_VIEW_DISTANCE) continue;
			if (tags.exists("hide-particles", p.getUniqueId())) continue;

			p.spawnParticle(particle, loc, count, offsetXZ, offsetY, offsetXZ, speed, blockData != null ? blockData : dustOptions);
		}
	}
	
	public void spawn(Player loc) {
		spawn(loc.getLocation());
	}
}
