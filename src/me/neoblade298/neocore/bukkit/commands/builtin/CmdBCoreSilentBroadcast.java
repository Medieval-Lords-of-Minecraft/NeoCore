package me.neoblade298.neocore.bukkit.commands.builtin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.neoblade298.neocore.bukkit.bungee.BungeeAPI;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class CmdBCoreSilentBroadcast extends Subcommand {

	public CmdBCoreSilentBroadcast(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		setDisplayArgs("[msg]");
	}

	@Override
	public void buildNode(LiteralArgumentBuilder<CommandSourceStack> node) {
		node.then(Commands.argument("msg", StringArgumentType.greedyString())
			.executes(ctx -> {
				BungeeAPI.broadcast(StringArgumentType.getString(ctx, "msg"));
				return Command.SINGLE_SUCCESS;
			}));
	}
}
