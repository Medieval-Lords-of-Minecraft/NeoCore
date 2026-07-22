package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.util.Util;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class CmdCoreRawMessage extends Subcommand {

	public CmdCoreRawMessage(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		setDisplayArgs("[player] [msg]");
	}

	@Override
	public void buildNode(LiteralArgumentBuilder<CommandSourceStack> node) {
		node.then(Commands.argument("player", StringArgumentType.word())
			.then(Commands.argument("msg", StringArgumentType.greedyString())
				.executes(ctx -> {
					CommandSender s = ctx.getSource().getSender();
					Player recipient = Bukkit.getPlayer(StringArgumentType.getString(ctx, "player"));
					String msg = StringArgumentType.getString(ctx, "msg");
					CommandSender target = recipient != null ? recipient : s;
					Util.msg(target, NeoCore.miniMessage().deserialize(msg), false);
					return Command.SINGLE_SUCCESS;
				})));
	}
}
