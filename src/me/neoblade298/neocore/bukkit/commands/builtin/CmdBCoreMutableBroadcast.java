package me.neoblade298.neocore.bukkit.commands.builtin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.bungee.BungeeAPI;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class CmdBCoreMutableBroadcast extends Subcommand {

	public CmdBCoreMutableBroadcast(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		setDisplayArgs("[mute tag] [msg]");
	}

	@Override
	public void buildNode(LiteralArgumentBuilder<CommandSourceStack> node) {
		node.then(Commands.argument("muteTag", StringArgumentType.word())
			.then(Commands.argument("msg", StringArgumentType.greedyString())
				.executes(ctx -> {
					String muteTag = StringArgumentType.getString(ctx, "muteTag");
					String msg = StringArgumentType.getString(ctx, "msg");
					BungeeAPI.mutableBroadcast(muteTag, NeoCore.miniMessage().deserialize(msg));
					return Command.SINGLE_SUCCESS;
				})));
	}
}
