package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;

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
	public void run(CommandSender s, String[] args) {
		if (NeoCore.toggleDebug()) {
			Util.msg(s, enable);
		}
		else {
			Util.msg(s, disable);
		}
	}
}
