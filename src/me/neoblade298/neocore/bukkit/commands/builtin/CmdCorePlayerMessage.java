package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class CmdCorePlayerMessage extends Subcommand {
	public CmdCorePlayerMessage(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		setDisplayArgs("[key]");
		hidden = true;
	}

	@Override
	public void buildNode(LiteralArgumentBuilder<CommandSourceStack> node) {
		node.then(Commands.argument("key", StringArgumentType.word())
			.executes(ctx -> {
				CommandSender s = ctx.getSource().getSender();
				CmdCoreSendMessage.parseAndRun(s, s, StringArgumentType.getString(ctx, "key"));
				return Command.SINGLE_SUCCESS;
			}));
	}
}
