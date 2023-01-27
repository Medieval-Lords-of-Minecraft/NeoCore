package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.bukkit.bungee.BungeeAPI;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.util.Util;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neocore.shared.util.SharedUtil;

public class CmdBCoreCmd extends Subcommand {

	public CmdBCoreCmd(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.setOverride("[cmd]");
	}

	@Override
	public void run(CommandSender s, String[] args) {
		if (args.length == 0) {
			Util.msg(s, "&cMust have a command to send!");
		}
		else {
			// Send cmd
			BungeeAPI.sendBungeeMessage(new String[] {"cmd", SharedUtil.connectArgs(args)});
		}
	}
}