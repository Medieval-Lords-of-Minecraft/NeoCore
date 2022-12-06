package me.neoblade298.neocore.commands.builtin;

import org.bukkit.command.CommandSender;
import me.neoblade298.neocore.bungee.BungeeAPI;
import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.util.Util;

public class CmdBCoreBroadcast implements Subcommand {

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
		return "bc";
	}
	
	@Override
	public String[] getAliases() {
		return new String[] {"broadcast"};
	}

	@Override
	public String getDescription() {
		return "Broadcasts a message on all servers";
	}

	@Override
	public String getArgOverride() {
		return "[msg]";
	}

	@Override
	public void run(CommandSender s, String[] args) {
		if (args.length == 0) {
			Util.msg(s, "&cMust have a message to send!");
		}
		else {
			// Convert args to msg
			String msg = args[0];
			for (int i = 1; i < args.length; i++) {
				msg += " " + args[i];
			}
			
			// Send msg
			BungeeAPI.broadcast(msg);
		}
	}

	@Override
	public CommandArguments getArgs() {
		return null;
	}
}
