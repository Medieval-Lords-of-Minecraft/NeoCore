package me.neoblade298.neocore.bungee.commands;

import java.util.TreeMap;

import org.jspecify.annotations.NonNull;

import me.neoblade298.neocore.shared.commands.AbstractSubcommandManager;
import net.kyori.adventure.text.format.TextColor;

public class CommandOverhead extends AbstractSubcommandManager<@NonNull Subcommand> {

	public CommandOverhead(String base, String perm, TextColor color) {
		super(base, perm, color);
	}
	
	public TreeMap<@NonNull String, @NonNull Subcommand> getHandlers() {
		return handlers;
	}
}
