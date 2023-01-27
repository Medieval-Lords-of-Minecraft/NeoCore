package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.util.Util;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class CmdCoreDebug extends Subcommand {
	public CmdCoreDebug(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
	}

	@Override
	public void run(CommandSender s, String[] args) {
		if (NeoCore.toggleDebug()) {
			Util.msg(s, "&7Successfully enabled debug mode!");
		}
		else {
			Util.msg(s, "&7Successfully disabled debug mode!");
		}
	}
}
