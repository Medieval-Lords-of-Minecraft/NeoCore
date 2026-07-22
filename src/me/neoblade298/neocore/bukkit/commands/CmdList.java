package me.neoblade298.neocore.bukkit.commands;

import java.util.HashSet;
import java.util.TreeMap;

import org.bukkit.command.CommandSender;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.neoblade298.neocore.shared.commands.SharedCmdList;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class CmdList extends Subcommand {
	private SharedCmdList<Subcommand> cmds;

	public CmdList(String key, String base, String perm, String basePerm, TreeMap<String, Subcommand> cmds, HashSet<String> aliases, TextColor defaultColor) {
		super(key, "List all commands", perm, SubcommandRunner.BOTH);
		this.cmds = new SharedCmdList<Subcommand>(key, base, basePerm, cmds, aliases, defaultColor);
		setDisplayArgs("{page}");
	}

	public CmdList(String key, String base, String perm, String basePerm, TreeMap<String, Subcommand> cmds, HashSet<String> aliases, TextColor defaultColor, TextColor cmdColor) {
		super(key, "List all commands", perm, SubcommandRunner.BOTH);
		this.cmds = new SharedCmdList<Subcommand>(key, base, basePerm, cmds, aliases, defaultColor);
		this.color = cmdColor;
		setDisplayArgs("{page}");
	}

	@Override
	public void buildNode(LiteralArgumentBuilder<CommandSourceStack> node) {
		node.executes(ctx -> {
			CommandSender s = ctx.getSource().getSender();
			for (Component comp : cmds.run(new String[0], p -> s.hasPermission(p))) {
				s.sendMessage(comp);
			}
			return Command.SINGLE_SUCCESS;
		}).then(Commands.argument("page", IntegerArgumentType.integer(1))
			.executes(ctx -> {
				CommandSender s = ctx.getSource().getSender();
				int page = IntegerArgumentType.getInteger(ctx, "page");
				for (Component comp : cmds.run(new String[] {String.valueOf(page)}, p -> s.hasPermission(p))) {
					s.sendMessage(comp);
				}
				return Command.SINGLE_SUCCESS;
			}));
	}
}
