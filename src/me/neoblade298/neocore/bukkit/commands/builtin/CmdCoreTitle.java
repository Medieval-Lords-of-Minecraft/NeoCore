package me.neoblade298.neocore.bukkit.commands.builtin;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

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
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;

public class CmdCoreTitle extends Subcommand {

	public CmdCoreTitle(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		setDisplayArgs("[player] {title} {subtitle}");
	}

	@Override
	public void buildNode(LiteralArgumentBuilder<CommandSourceStack> node) {
		node.then(Commands.argument("player", StringArgumentType.word())
			// /ncore title <player>
			.executes(ctx -> sendTitle(ctx.getSource().getSender(),
					ctx.getArgument("player", String.class), "", ""))
			.then(Commands.argument("titleStr", StringArgumentType.word())
				// /ncore title <player> <title>
				.executes(ctx -> sendTitle(ctx.getSource().getSender(),
						ctx.getArgument("player", String.class),
						ctx.getArgument("titleStr", String.class), ""))
				.then(Commands.argument("subtitle", StringArgumentType.word())
					// /ncore title <player> <title> <subtitle>
					.executes(ctx -> sendTitle(ctx.getSource().getSender(),
							ctx.getArgument("player", String.class),
							ctx.getArgument("titleStr", String.class),
							ctx.getArgument("subtitle", String.class))))));
	}

	private static int sendTitle(CommandSender s, String playerName, String titleStr, String subtitleStr) {
		Player p = Bukkit.getPlayer(playerName);
		if (p == null) { s.sendMessage("\u00a7cThat player is not online!"); return 0; }
		Times times = Times.times(
				Duration.of(1, ChronoUnit.SECONDS),
				Duration.of(3, ChronoUnit.SECONDS),
				Duration.of(1, ChronoUnit.SECONDS));
		p.showTitle(Title.title(
				NeoCore.miniMessage().deserialize(titleStr),
				NeoCore.miniMessage().deserialize(subtitleStr),
				times));
		return Command.SINGLE_SUCCESS;
	}
}
