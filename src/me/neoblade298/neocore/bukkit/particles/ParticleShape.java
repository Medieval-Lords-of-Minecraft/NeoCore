package me.neoblade298.neocore.bukkit.particles;

import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Player;

public abstract class ParticleShape {
	public abstract void draw(Player p, boolean showAllPlayers, Particle part, DustOptions options);
	public abstract void drawEdges(Player p, boolean showAllPlayers, Particle part, DustOptions options);
}
