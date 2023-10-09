package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.util.Util;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neocore.shared.util.SharedUtil;

public class CmdCoreBroadcast extends Subcommand {
	public CmdCoreBroadcast(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.setOverride("[broadcast]");
	}

	@Override
	public void run(CommandSender s, String[] args) {
		if (args.length == 0) {
			Util.msg(s, "&cYou need a message to broadcast!");
		}
		else {
			Bukkit.broadcast(message);
			Bukkit.broadcastMessage(SharedUtil.translateColors("&4[&c&lMLMC&4] " + SharedUtil.connectArgs(args)));
		}
	}
}
