package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.neoblade298.neocore.bukkit.bungee.BungeeAPI;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class CmdBCoreSend extends Subcommand {
	public CmdBCoreSend(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		setDisplayArgs("{player} [server]");
	}

	@Override
	public void buildNode(LiteralArgumentBuilder<CommandSourceStack> node) {
		// /bcore send <server>  - sends the command sender
		node.then(Commands.argument("server", StringArgumentType.word())
				.executes(ctx -> {
					Player p = (Player) ctx.getSource().getSender();
					BungeeAPI.sendPlayer(p, StringArgumentType.getString(ctx, "server"));
					return Command.SINGLE_SUCCESS;
				}))
			// /bcore send <player> <server>  - sends a named player
			.then(Commands.argument("player", StringArgumentType.word())
				.then(Commands.argument("serverTarget", StringArgumentType.word())
					.executes(ctx -> {
						String playerName = StringArgumentType.getString(ctx, "player");
						String server = StringArgumentType.getString(ctx, "serverTarget");
						BungeeAPI.sendPlayer(Bukkit.getPlayer(playerName), server);
						return Command.SINGLE_SUCCESS;
					})));
	}
}