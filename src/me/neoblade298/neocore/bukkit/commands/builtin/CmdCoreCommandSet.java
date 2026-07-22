package me.neoblade298.neocore.bukkit.commands.builtin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.commandsets.CommandSetManager;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class CmdCoreCommandSet extends Subcommand {

	public CmdCoreCommandSet(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		setDisplayArgs("[key] {args}");
	}

	@Override
	public void buildNode(LiteralArgumentBuilder<CommandSourceStack> node) {
		node.then(Commands.argument("key", StringArgumentType.word())
			.executes(ctx -> {
				CommandSetManager.runSet(StringArgumentType.getString(ctx, "key"), new String[0]);
				return Command.SINGLE_SUCCESS;
			})
			.then(Commands.argument("args", StringArgumentType.greedyString())
				.executes(ctx -> {
					String key = StringArgumentType.getString(ctx, "key");
					String[] extraArgs = StringArgumentType.getString(ctx, "args").split(" ");
					CommandSetManager.runSet(key, extraArgs);
					return Command.SINGLE_SUCCESS;
				})));
	}
}
