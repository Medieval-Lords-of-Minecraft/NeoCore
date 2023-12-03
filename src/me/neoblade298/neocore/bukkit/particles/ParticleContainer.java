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
	private double offsetX, offsetY, offsetZ, speed;
	private BlockData blockData;
	private DustOptions dustOptions;
	
	public ParticleContainer(Particle particle, Player audience, boolean showAllPlayers, int count) {
		this.particle = particle;
		this.audience = audience;
		this.showAllPlayers = showAllPlayers;
		this.count = count;
	}
	
	public void setup(double offsetX, double offsetY, double offsetZ) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
	}
	
	public void setup(double speed) {
		this.speed = speed;
	}
	
	public void setup(BlockData blockData) {
		this.blockData = blockData;
	}
	
	public void setup(DustOptions dustOptions) {
		this.dustOptions = dustOptions;
	}
	
	public void spawn(Player p) {
		spawn(p.getLocation());
	}
	
	private void spawn(Location loc) {
		if (showAllPlayers) {
			loc.getWorld().spawnParticle(particle, loc, count, offsetX, offsetY, offsetZ, speed, blockData != null ? blockData : dustOptions);
		}
		else {
			audience.spawnParticle(particle, loc, count, offsetX, offsetY, offsetZ, speed, blockData != null ? blockData : dustOptions);
		}
	}
}
