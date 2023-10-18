package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.io.IOType;
import me.neoblade298.neocore.bukkit.io.PlayerIOManager;
import me.neoblade298.neocore.bukkit.util.Util;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.format.NamedTextColor;

public class CmdIODisabled extends Subcommand {
	public CmdIODisabled(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
	}

	@Override
	public void run(CommandSender s, String[] args) {
		for (IOType type : IOType.values()) {
			Builder b = Component.text();
			b.content(type.name()).color(NamedTextColor.GOLD).append(Component.text(":", NamedTextColor.GRAY));
			for (String key : PlayerIOManager.getDisabledIO().get(type)) {
				b.append(Component.text(" " + key, NamedTextColor.YELLOW));
			}
			Util.msg(s, b.build(), false);
		}
	}
}
