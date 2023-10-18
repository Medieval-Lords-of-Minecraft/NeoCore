package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.io.IOComponentWrapper;
import me.neoblade298.neocore.bukkit.io.PlayerIOManager;
import me.neoblade298.neocore.bukkit.util.Util;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.format.NamedTextColor;

public class CmdIOList extends Subcommand {
	public CmdIOList(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
	}

	@Override
	public void run(CommandSender s, String[] args) {
		for (IOComponentWrapper io : PlayerIOManager.getComponents()) {
			Builder b = Component.text();
			b.content("- ").color(NamedTextColor.GRAY)
			.append(Component.text(io.getKey() + " (", NamedTextColor.GOLD))
			.append(Component.text(io.getPriority(), NamedTextColor.YELLOW))
			.append(Component.text(")", NamedTextColor.GOLD));
			Util.msg(s, b.build(), false);
		}
	}
}
