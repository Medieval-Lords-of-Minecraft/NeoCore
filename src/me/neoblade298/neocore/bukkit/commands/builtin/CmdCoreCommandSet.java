package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.commandsets.CommandSetManager;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class CmdCoreCommandSet extends Subcommand {

	public CmdCoreCommandSet(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.setOverride("[key] {args}");
		args.setMin(1);
	}

	@Override
	public void run(CommandSender s, String[] args) {
		String[] newArgs = new String[args.length - 1];
		for (int i = 1; i < args.length; i++) {
			newArgs[i - 1] = args[i];
		}
		CommandSetManager.runSet(args[0], newArgs);
	}
}
