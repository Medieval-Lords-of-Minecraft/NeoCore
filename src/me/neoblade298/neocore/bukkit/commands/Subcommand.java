package me.neoblade298.neocore.bukkit.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.neoblade298.neocore.shared.commands.AbstractSubcommand;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public abstract class Subcommand extends AbstractSubcommand {
	public Subcommand(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
	}

	/**
	 * Attach argument nodes and executes() handlers to the provided literal.
	 * For base-command subcommands (key=""), the literal is the root command node.
	 */
	public abstract void buildNode(LiteralArgumentBuilder<CommandSourceStack> node);
}
