package me.neoblade298.neocore.velocity.commands;

import java.util.List;

import com.velocitypowered.api.proxy.Player;

public interface VelocityTabResolver {
	public List<String> resolve(Player p);
}
