package me.neoblade298.neocore.bukkit.particles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class PrivateParticleContainer extends ParticleContainer {
	protected Player audience;
	public PrivateParticleContainer(Particle particle, Player audience) {
		super(particle);
		this.audience = audience;
	}
	
	public PrivateParticleContainer(ParticleContainer container, Player audience) {
		super(container.particle);
		this.blockData = container.blockData;
		this.count = container.count;
		this.dustOptions = container.dustOptions;
		this.offsetXZ = container.offsetXZ;
		this.offsetY = container.offsetY;
		this.speed = container.speed;
		this.audience = audience;
	}

	@Override
	public void spawn(Location loc) {
		audience.spawnParticle(particle, loc, count, offsetXZ, offsetY, offsetXZ, speed, blockData != null ? blockData : dustOptions);
	}
}
