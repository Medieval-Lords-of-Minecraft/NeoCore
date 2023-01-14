package me.neoblade298.neocore.commands.builtin;

import org.bukkit.command.CommandSender;
import me.neoblade298.neocore.bungee.BungeeAPI;
import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.util.Util;

public class CmdBCoreCmd implements Subcommand {

	@Override
	public String getPermission() {
		return "mycommand.admin";
	}

	@Override
	public SubcommandRunner getRunner() {
		return SubcommandRunner.BOTH;
	}

	@Override
	public String getKey() {
		return "cmd";
	}

	@Override
	public String getDescription() {
		return "Sends a command for the bungee console to use";
	}

	@Override
	public String getArgOverride() {
		return "[cmd]";
	}

	@Override
	public void run(CommandSender s, String[] args) {
		if (args.length == 0) {
			Util.msg(s, "&cMust have a command to send!");
		}
		else {
			// Send cmd
			BungeeAPI.sendBungeeMessage(new String[] {"cmd", Util.connectArgs(args)});
		}
	}

	@Override
	public CommandArguments getArgs() {
		return null;
	}
}
