package me.neoblade298.neocore.commands.builtin;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.commandsets.CommandSetManager;

public class CmdCoreCommandSet implements Subcommand {

	@Override
	public String getPermission() {
		return "mycommand.staff";
	}

	@Override
	public SubcommandRunner getRunner() {
		return SubcommandRunner.BOTH;
	}

	@Override
	public String getKey() {
		return "commandset";
	}

	@Override
	public String getDescription() {
		return "Runs a command set";
	}

	@Override
	public void run(CommandSender s, String[] args) {
		String[] newArgs = new String[args.length - 1];
		for (int i = 1; i < args.length; i++) {
			newArgs[i - 1] = args[i];
		}
		CommandSetManager.runSet(args[0], newArgs);
	}

	@Override
	public String getArgOverride() {
		return "[key] {args}";
	}
}
