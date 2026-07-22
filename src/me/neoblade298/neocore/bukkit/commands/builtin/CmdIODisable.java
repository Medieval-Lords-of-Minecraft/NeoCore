package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.io.IOType;
import me.neoblade298.neocore.bukkit.io.PlayerIOManager;
import me.neoblade298.neocore.bukkit.util.Util;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CmdIODisable extends Subcommand {
	public CmdIODisable(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		setDisplayArgs("[action] {io key}");
	}

	@Override
	public void buildNode(LiteralArgumentBuilder<CommandSourceStack> node) {
		node.then(Commands.argument("action", StringArgumentType.word())
			.executes(ctx -> {
				CommandSender s = ctx.getSource().getSender();
				IOType type = IOType.valueOf(StringArgumentType.getString(ctx, "action").toUpperCase());
				PlayerIOManager.disableIO(type);
				Util.msg(s, Component.text("Successfully set " + type + " to disabled.", NamedTextColor.GRAY));
				return Command.SINGLE_SUCCESS;
			})
			.then(Commands.argument("ioKey", StringArgumentType.word())
				.executes(ctx -> {
					CommandSender s = ctx.getSource().getSender();
					IOType type = IOType.valueOf(StringArgumentType.getString(ctx, "action").toUpperCase());
					String ioKey = StringArgumentType.getString(ctx, "ioKey");
					PlayerIOManager.disableIO(type, ioKey);
					Util.msg(s, Component.text("Successfully set " + type + " to disabled for manager " + ioKey + ".", NamedTextColor.GRAY));
					return Command.SINGLE_SUCCESS;
				})));
	}
}
