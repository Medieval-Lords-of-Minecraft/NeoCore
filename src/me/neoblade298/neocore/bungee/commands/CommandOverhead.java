package me.neoblade298.neocore.bungee.commands;

import java.util.TreeMap;

import me.neoblade298.neocore.shared.commands.AbstractSubcommandManager;
import net.kyori.adventure.text.format.TextColor;

public class CommandOverhead extends AbstractSubcommandManager<Subcommand> {

	public CommandOverhead(String base, String perm, TextColor color) {
		super(base, perm, color);
	}
	
	public TreeMap<String, Subcommand> getHandlers() {
		return handlers;
	}
}
