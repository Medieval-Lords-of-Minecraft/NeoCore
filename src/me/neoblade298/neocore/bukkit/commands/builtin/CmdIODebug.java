package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.io.PlayerIOManager;
import me.neoblade298.neocore.bukkit.util.BukkitUtil;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class CmdIODebug extends Subcommand {
	public CmdIODebug(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
	}

	@Override
	public void run(CommandSender s, String[] args) {
		if (PlayerIOManager.toggleDebug()) {
			BukkitUtil.msg(s, "&7Successfully enabled io debug mode!");
		}
		else {
			BukkitUtil.msg(s, "&7Successfully disabled io debug mode!");
		}
	}
}
