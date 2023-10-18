package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.io.PlayerIOManager;
import me.neoblade298.neocore.bukkit.util.Util;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CmdIODebug extends Subcommand {
	private static Component enable = Component.text("Successfully enabled io debug mode!", NamedTextColor.GRAY);
	private static Component disable = Component.text("Successfully disabled io debug mode!", NamedTextColor.GRAY);
	public CmdIODebug(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
	}

	@Override
	public void run(CommandSender s, String[] args) {
		if (PlayerIOManager.toggleDebug()) {
			Util.msg(s, enable);
		}
		else {
			Util.msg(s, disable);
		}
	}
}
