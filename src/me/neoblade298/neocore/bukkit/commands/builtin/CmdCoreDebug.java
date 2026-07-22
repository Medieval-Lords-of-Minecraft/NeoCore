package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.util.Util;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CmdCoreDebug extends Subcommand {
	private static Component enable = Component.text("Successfully enabled debug mode!", NamedTextColor.GRAY);
	private static Component disable = Component.text("Successfully disabled debug mode!", NamedTextColor.GRAY);

	public CmdCoreDebug(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
	}

	@Override
	public void buildNode(LiteralArgumentBuilder<CommandSourceStack> node) {
		node.executes(ctx -> {
			CommandSender s = ctx.getSource().getSender();
			Util.msg(s, NeoCore.toggleDebug() ? enable : disable);
			return Command.SINGLE_SUCCESS;
		});
	}
}
