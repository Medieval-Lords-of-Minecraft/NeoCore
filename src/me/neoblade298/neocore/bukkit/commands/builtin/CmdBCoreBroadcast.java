package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.bukkit.bungee.BungeeAPI;
import me.neoblade298.neocore.bukkit.commands.CommandArguments;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.commands.SubcommandRunner;
import me.neoblade298.neocore.shared.util.SharedUtil;

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
			SharedUtil.msg(s, "&cMust have a message to send!");
		}
		else {
			// Send msg
			BungeeAPI.broadcast(SharedUtil.connectArgs(args));
		}
	}

	@Override
	public CommandArguments getArgs() {
		return null;
	}
}
