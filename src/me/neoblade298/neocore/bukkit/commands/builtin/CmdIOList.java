package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.io.IOComponentWrapper;
import me.neoblade298.neocore.bukkit.io.PlayerIOManager;
import me.neoblade298.neocore.bukkit.util.BukkitUtil;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class CmdIOList extends Subcommand {
	public CmdIOList(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
	}

	@Override
	public void run(CommandSender s, String[] args) {
		for (IOComponentWrapper io : PlayerIOManager.getComponents()) {
			BukkitUtil.msg(s, "&7- &6" + io.getKey() + " (&e" + io.getPriority() + "&6)", false);
		}
	}
}
