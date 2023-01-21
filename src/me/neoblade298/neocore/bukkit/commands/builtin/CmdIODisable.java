package me.neoblade298.neocore.bukkit.commands.builtin;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.bukkit.commands.CommandArgument;
import me.neoblade298.neocore.bukkit.commands.CommandArguments;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.commands.SubcommandRunner;
import me.neoblade298.neocore.bukkit.io.IOType;
import me.neoblade298.neocore.bukkit.io.PlayerIOManager;
import me.neoblade298.neocore.util.Util;

public class CmdIODisable implements Subcommand {
	private static final CommandArguments args = new CommandArguments(Arrays.asList(new CommandArgument("action", true),
			new CommandArgument("io key", false)));

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public SubcommandRunner getRunner() {
		return SubcommandRunner.BOTH;
	}

	@Override
	public String getKey() {
		return "disable";
	}

	@Override
	public String getDescription() {
		return "Disables an IO action: save, preload, load, cleanup";
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

	@Override
	public CommandArguments getArgs() {
		return args;
	}
}
