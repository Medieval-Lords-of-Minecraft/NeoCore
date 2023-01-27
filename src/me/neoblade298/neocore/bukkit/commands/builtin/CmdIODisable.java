package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.io.IOType;
import me.neoblade298.neocore.bukkit.io.PlayerIOManager;
import me.neoblade298.neocore.bukkit.util.Util;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class CmdIODisable extends Subcommand {
	public CmdIODisable(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("action", true), new Arg("io key", false));
	}

	@Override
	public void run(CommandSender s, String[] args) {
		IOType type = IOType.valueOf(args[0].toUpperCase());
		if (args.length == 1) {
			PlayerIOManager.disableIO(type);
			Util.msg(s, "Successfully set " + type + " to disabled.");
		}
		else {
			PlayerIOManager.disableIO(type, args[1]);
			Util.msg(s, "Successfully set " + type + " to disabled for manager " + args[1] + ".");
		}
	}
}
