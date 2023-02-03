package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.bukkit.bungee.BungeeAPI;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.util.Util;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neocore.shared.util.SharedUtil;

public class CmdBCoreSilentMutableBroadcast extends Subcommand {

	public CmdBCoreSilentMutableBroadcast(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.setOverride("[mute tag] [msg]");
		args.setMin(2);
	}

	@Override
	public void run(CommandSender s, String[] args) {
		BungeeAPI.mutableBroadcast(args[0], SharedUtil.connectArgs(args, 1), false);
	}
}
