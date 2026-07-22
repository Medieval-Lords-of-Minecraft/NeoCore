package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.shared.chat.MiniMessageManager;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class CmdCoreSendMessage extends Subcommand {
	public CmdCoreSendMessage(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		setDisplayArgs("{player} [key]");
	}

	@Override
	public void buildNode(LiteralArgumentBuilder<CommandSourceStack> node) {
		// /ncore sendmsg <key>  - sends to self
		node.then(Commands.argument("key", StringArgumentType.word())
				.executes(ctx -> {
					CommandSender s = ctx.getSource().getSender();
					s.sendMessage(MiniMessageManager.get(StringArgumentType.getString(ctx, "key")));
					return Command.SINGLE_SUCCESS;
				}))
			// /ncore sendmsg <player> <key>  - sends to player
			.then(Commands.argument("player", StringArgumentType.word())
				.then(Commands.argument("msgKey", StringArgumentType.word())
					.executes(ctx -> {
						CommandSender s = ctx.getSource().getSender();
						CommandSender recipient = Bukkit.getPlayer(StringArgumentType.getString(ctx, "player"));
						if (recipient == null) recipient = s;
						recipient.sendMessage(MiniMessageManager.get(StringArgumentType.getString(ctx, "msgKey")));
						return Command.SINGLE_SUCCESS;
					})));
	}

	/** Utility kept for internal callers. */
	public static void parseAndRun(CommandSender s, CommandSender recipient, String key) {
		s.sendMessage(MiniMessageManager.get(key));
	}
}
