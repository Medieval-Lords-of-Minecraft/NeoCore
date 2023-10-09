package me.neoblade298.neocore.bungee.commands;

import java.util.List;

import com.velocitypowered.api.proxy.Player;

public interface BungeeTabResolver {
	public List<String> resolve(Player p);
}
