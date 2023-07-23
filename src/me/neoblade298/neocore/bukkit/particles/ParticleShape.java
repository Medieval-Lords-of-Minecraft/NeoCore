package me.neoblade298.neocore.bukkit.particles;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Player;

public abstract class ParticleShape {
	public abstract void draw(Player p, boolean showAllPlayers, Particle part, DustOptions options);
}
