package me.neoblade298.neocore.bukkit.commands.builtin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.neoblade298.neocore.bukkit.bungee.BungeeAPI;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class CmdBCoreBroadcast extends Subcommand {

	public CmdBCoreBroadcast(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		aliases = new String[] {"broadcast"};
		setDisplayArgs("[msg]");
	}

	@Override
	public void buildNode(LiteralArgumentBuilder<CommandSourceStack> node) {
		node.then(Commands.argument("msg", StringArgumentType.greedyString())
			.executes(ctx -> {
				String msg = StringArgumentType.getString(ctx, "msg");
				BungeeAPI.broadcast("<dark_red>[<red><bold>MLMC</bold></red>] <gray>" + msg);
				return Command.SINGLE_SUCCESS;
			}));
	}
}
