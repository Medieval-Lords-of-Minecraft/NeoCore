package me.neoblade298.neocore.bukkit.util;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class SoundContainer {
	private float volume, pitch;
	private Sound sound;
	
	public SoundContainer(Sound sound) {
		this(sound, 1F, 1F);
	}
	
	public SoundContainer(Sound sound, float pitch) {
		this(sound, pitch, 1F);
	}
	
	public SoundContainer(Sound sound, float pitch, float volume) {
		this.sound = sound;
		this.pitch = pitch;
		this.volume = volume;
	}
	
	public void play(Player p) {
		p.playSound(p, sound, volume, pitch);
	}
	
	public void play(Player p, Location loc) {
		p.playSound(loc, sound, volume, pitch);
	}
	
	public void playGlobal(Entity e) {
		e.getWorld().playSound(e, sound, volume, pitch);
	}
	
	public void playGlobal(Location loc) {
		loc.getWorld().playSound(loc, sound, volume, pitch);
	}
}
