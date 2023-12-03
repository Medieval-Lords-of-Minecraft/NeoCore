package me.neoblade298.neocore.bukkit.particles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

public class ParticleContainer {
	private Player audience;
	private Particle particle;
	private boolean showAllPlayers;
	private int count;
	private double offsetXZ, offsetY, speed;
	private BlockData blockData;
	private DustOptions dustOptions;
	
	public ParticleContainer(Particle particle, Player audience, boolean showAllPlayers) {
		this.particle = particle;
		this.audience = audience;
		this.showAllPlayers = showAllPlayers;
	}
	
	public ParticleContainer clone() {
		ParticleContainer pc = new ParticleContainer(particle, audience, showAllPlayers);
		pc.setCount(count);
		pc.setOffset(offsetXZ, offsetY);
		pc.setSpeed(speed);
		pc.setBlockData(blockData);
		pc.setDustOptions(dustOptions);
		return pc;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	public void setOffset(double offsetXZ, double offsetY) {
		this.offsetXZ = offsetXZ;
		this.offsetY = offsetY;
	}
	
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	public void setBlockData(BlockData blockData) {
		this.blockData = blockData;
	}
	
	public void setDustOptions(DustOptions dustOptions) {
		this.dustOptions = dustOptions;
	}
	
	public void spawn(Player p) {
		spawn(p.getLocation());
	}
	
	public void spawn(Location loc) {
		if (showAllPlayers) {
			loc.getWorld().spawnParticle(particle, loc, count, offsetXZ, offsetY, offsetXZ, speed, blockData != null ? blockData : dustOptions);
		}
		else {
			audience.spawnParticle(particle, loc, count, offsetXZ, offsetY, offsetXZ, speed, blockData != null ? blockData : dustOptions);
		}
	}
}
