package me.neoblade298.neocore.bukkit.particles;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.neoblade298.neocore.bukkit.NeoCore;

public class ParticleAnimation {
	private ParticleContainer particle;
	private int steps, frequency;
	private ParticleFormula formula;
	
	public ParticleAnimation(ParticleContainer particle, ParticleFormula formula, int steps, int frequency) {
		this.particle = particle;
		this.formula = formula;
		this.steps = steps;
		this.frequency = frequency;
	}
	
	public ParticleAnimation(ParticleContainer particle, ParticleFormula formula, int duration) {
		this(particle, formula, duration, 1);
	}
	
	private interface ParticleFormula {
		public Location run(int step);
	}
	
	private class ParticleAnimationInstance {
		private ArrayList<BukkitTask> tasks;
		
		public ParticleAnimationInstance(ParticleAnimation anim) {
			tasks = new ArrayList<BukkitTask>(anim.steps);
			for (int i = 0; i < anim.steps; i++) {
				final int step = i;
				tasks.add(new BukkitRunnable() {
					public void run() {
						anim.formula.run(step);
					}
				}.runTaskLater(NeoCore.inst(), i * anim.frequency));
			}
		}
		
		public void cancel() {
			for (BukkitTask task : tasks) {
				task.cancel();
			}
		}
	}
}
