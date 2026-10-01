package me.neoblade298.neocore.bukkit.effects;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ParticleContainer extends Effect {
	public static final String HIDE_TAG = "hide-particles";
	protected Particle particle;
	protected int count = 1;
	protected double spreadXZ, spreadY, speed, offsetForward, offsetForwardAngle, offsetY;
	protected boolean offsetForwardUseOriginalY = true;
	protected BlockData blockData;
	protected DustOptions dustOptions;
	protected Color color;
	
	public ParticleContainer(Particle particle) {
		super(HIDE_TAG);
		particle(particle);
	}
	
	public ParticleContainer clone() {
		ParticleContainer pc = new ParticleContainer(particle);
		pc.count(count);
		pc.spread(spreadXZ, spreadY);
		pc.speed(speed);
		pc.blockData = blockData;
		pc.dustOptions = dustOptions;
		pc.color = color;
		pc.forceVisibility = forceVisibility;
		pc.offsetY = offsetY;
		pc.offsetForward = offsetForward;
		pc.offsetForwardAngle = offsetForwardAngle;
		pc.offsetForwardUseOriginalY = offsetForwardUseOriginalY;
		return pc;
	}
	
	public ParticleContainer particle(Particle particle) {
		if (particle == null) {
			throw new IllegalArgumentException("Particle cannot be null");
		}
		this.particle = particle;
		this.blockData = null;
		this.dustOptions = particle == Particle.DUST ? new DustOptions(Color.RED, count) : null;
		this.color = particle == Particle.FLASH ? Color.WHITE : null;
		return this;
	}
	
	public ParticleContainer count(int count) {
		if (count < 0) {
			throw new IllegalArgumentException("Particle count cannot be negative");
		}
		this.count = count;
		return this;
	}
	
	public ParticleContainer forceVisible(Audience forced) {
		this.forceVisibility = forced;
		return this;
	}
	
	public ParticleContainer spread(double spreadXZ, double spreadY) {
		validateFiniteNonNegative(spreadXZ, "Horizontal spread");
		validateFiniteNonNegative(spreadY, "Vertical spread");
		this.spreadXZ = spreadXZ;
		this.spreadY = spreadY;
		return this;
	}
	
	public ParticleContainer offsetY(double offsetY) {
		validateFinite(offsetY, "Vertical offset");
		this.offsetY = offsetY;
		return this;
	}
	
	public ParticleContainer offsetForward(double offsetForward) {
		validateFinite(offsetForward, "Forward offset");
		this.offsetForward = offsetForward;
		return this;
	}
	
	public ParticleContainer offsetForward(double offsetForward, double offsetForwardAngle) {
		validateFinite(offsetForward, "Forward offset");
		validateFinite(offsetForwardAngle, "Forward offset angle");
		this.offsetForward = offsetForward;
		this.offsetForwardAngle = offsetForwardAngle;
		return this;
	}
	
	public ParticleContainer offsetForward(double offsetForward, double offsetForwardAngle, boolean offsetForwardUseOriginalY) {
		offsetForward(offsetForward, offsetForwardAngle);
		this.offsetForwardUseOriginalY = offsetForwardUseOriginalY;
		return this;
	}
	
	public ParticleContainer speed(double speed) {
		validateFiniteNonNegative(speed, "Particle speed");
		this.speed = speed;
		return this;
	}
	
	public ParticleContainer blockData(BlockData blockData) {
		this.dustOptions = null;
		this.color = null;
		this.blockData = blockData;
		return this;
	}
	
	public ParticleContainer dustOptions(DustOptions dustOptions) {
		this.blockData = null;
		this.color = null;
		this.dustOptions = dustOptions;
		return this;
	}

	public ParticleContainer color(Color color) {
		this.blockData = null;
		this.dustOptions = null;
		this.color = color;
		return this;
	}
	
	private Location calculateOffset(Location loc) {
		if (offsetY == 0 && offsetForward == 0) return loc;

		Location offset = loc.clone().add(0, offsetY, 0);
		if (offsetForward == 0) return offset;

		Vector forward;
		if (offsetForwardUseOriginalY) {
			forward = loc.getDirection();
		}
		else {
			Location horizontal = loc.clone();
			horizontal.setPitch(0);
			forward = horizontal.getDirection();
		}
		forward.rotateAroundY(offsetForwardAngle);
		return offset.add(forward.multiply(offsetForward));
	}
	
	@Override
	public void playEffect(Player p, Location loc) {
		p.spawnParticle(particle, calculateOffset(loc), count, spreadXZ, spreadY, spreadXZ, speed, getData());
	}

	@Override
	public void playWithCache(List<Player> cache, Location loc) {
		if (cache.isEmpty()) return;

		Location offset = calculateOffset(loc);
		Object data = getData();
		offset.getWorld().spawnParticle(
				particle,
				cache,
				null,
				offset.getX(),
				offset.getY(),
				offset.getZ(),
				count,
				spreadXZ,
				spreadY,
				spreadXZ,
				speed,
				data,
				false);
	}

	@Override
	protected void playEffect(Location loc) {
		loc.getWorld().spawnParticle(particle, calculateOffset(loc), count, spreadXZ, spreadY, spreadXZ, speed, getData());
	}

	private Object getData() {
		if (blockData != null) return blockData;
		if (dustOptions != null) return dustOptions;
		return color;
	}

	private static void validateFinite(double value, String name) {
		if (!Double.isFinite(value)) {
			throw new IllegalArgumentException(name + " must be finite");
		}
	}

	private static void validateFiniteNonNegative(double value, String name) {
		if (!Double.isFinite(value) || value < 0) {
			throw new IllegalArgumentException(name + " must be finite and non-negative");
		}
	}
}
