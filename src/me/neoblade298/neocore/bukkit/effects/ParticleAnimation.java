package me.neoblade298.neocore.bukkit.effects;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.neoblade298.neocore.bukkit.NeoCore;

public class ParticleAnimation {
	private static final int AUDIENCE_REFRESH_TICKS = 20;

	private ParticleContainer particle;
	private int steps, frequency;
	private ParticleFormula formula;
	
	public ParticleAnimation(ParticleContainer particle, ParticleFormula formula, int steps, int frequency) {
		if (steps <= 0) {
			throw new IllegalArgumentException("Animation steps must be greater than zero");
		}
		if (frequency <= 0) {
			throw new IllegalArgumentException("Animation frequency must be greater than zero");
		}
		this.particle = Objects.requireNonNull(particle, "Particle cannot be null");
		this.formula = Objects.requireNonNull(formula, "Particle formula cannot be null");
		this.steps = steps;
		this.frequency = frequency;
	}
	
	public ParticleAnimation(ParticleContainer particle, ParticleFormula formula, int duration) {
		this(particle, formula, duration, 1);
	}
	
	public ParticleAnimationInstance play(Player origin, Entity ent) {
		return new ParticleAnimationInstance(origin, this, ent);
	}
	
	public ParticleAnimationInstance play(Player origin, Location loc) {
		return new ParticleAnimationInstance(origin, this, loc);
	}
	
	public interface ParticleFormula {
		public LinkedList<Location> run(Location center, int step);
	}
	
	public class ParticleAnimationInstance {
		private BukkitTask task;
		private Player origin;
		private List<Player> cache;
		private Entity ent;
		private Location loc;
		
		private ParticleAnimationInstance(Player origin, ParticleAnimation anim, Location loc) {
			this.origin = origin;
			this.loc = loc.clone();
			this.cache = calculateCache(anim, this.loc);
			run(anim);
		}
		
		private ParticleAnimationInstance(Player origin, ParticleAnimation anim, Entity ent) {
			this.origin = origin;
			this.ent = ent;
			this.cache = calculateCache(anim, ent.getLocation());
			run(anim);
		}
		
		private void run(ParticleAnimation anim) {
			task = new BukkitRunnable() {
				private int step;
				private long nextAudienceRefresh = AUDIENCE_REFRESH_TICKS;

				@Override
				public void run() {
					if (ent != null && !ent.isValid()) {
						cancel();
						return;
					}

					Location center = ent == null ? loc : ent.getLocation();
					long elapsedTicks = (long) step * anim.frequency;
					if (elapsedTicks >= nextAudienceRefresh) {
						cache = calculateCache(anim, center);
						do {
							nextAudienceRefresh += AUDIENCE_REFRESH_TICKS;
						}
						while (elapsedTicks >= nextAudienceRefresh);
					}

					LinkedList<Location> locations = Objects.requireNonNull(
							anim.formula.run(center, step),
							"Particle formula returned null at step " + step);
					ParticleUtil.ensurePointBudget(locations.size());
					for (Location location : locations) {
						anim.particle.playWithCache(cache, location);
					}

					step++;
					if (step >= anim.steps) {
						cancel();
					}
				}
			}.runTaskTimer(NeoCore.inst(), 0, anim.frequency);
		}

		private List<Player> calculateCache(ParticleAnimation anim, Location center) {
			return Effect.calculateCache(origin, center, anim.particle.forceVisibility, ParticleContainer.HIDE_TAG);
		}
		
		public void cancel() {
			task.cancel();
		}
	}
}
