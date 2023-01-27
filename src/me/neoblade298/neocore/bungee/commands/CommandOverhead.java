package me.neoblade298.neocore.bungee.commands;

import java.util.TreeMap;

import me.neoblade298.neocore.shared.commands.AbstractSubcommandManager;
import net.md_5.bungee.api.ChatColor;

public class CommandOverhead extends AbstractSubcommandManager<Subcommand> {

	public CommandOverhead(String base, String perm, ChatColor color) {
		super(base, perm, color);
	}
	
	public TreeMap<String, Subcommand> getHandlers() {
		return handlers;
	}
}
