package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.bukkit.bungee.BungeeAPI;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.util.Util;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neocore.shared.util.SharedUtil;

public class CmdBCoreBroadcast extends Subcommand {

	public CmdBCoreBroadcast(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		aliases = new String[] {"broadcast"};
		args.setOverride("[msg]");
	}

	@Override
	public void run(CommandSender s, String[] args) {
		if (args.length == 0) {
			Util.msg(s, "&cMust have a message to send!");
		}
		else {
			// Send msg
			BungeeAPI.broadcast("&4[&c&lMLMC&4] &7" + SharedUtil.connectArgs(args));
		}
	}
}
