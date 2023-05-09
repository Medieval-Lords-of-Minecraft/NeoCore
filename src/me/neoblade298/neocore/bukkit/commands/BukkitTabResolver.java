package me.neoblade298.neocore.bukkit.commands;

import java.util.List;

import org.bukkit.entity.Player;

public interface BukkitTabResolver {
	public List<String> resolve(Player p);
}
