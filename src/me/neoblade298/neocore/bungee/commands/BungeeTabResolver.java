package me.neoblade298.neocore.bungee.commands;

import java.util.List;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface BungeeTabResolver {
	public List<String> resolve(ProxiedPlayer p);
}
