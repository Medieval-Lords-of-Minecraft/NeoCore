package me.neoblade298.neocore.bungee.commands.builtin;

import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.util.SharedUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class CmdSilentMutableBroadcast extends Command {
	public CmdSilentMutableBroadcast() {
		super("smbc");
	}

	@Override
	public void execute(CommandSender s, String[] args) {
		if (!s.hasPermission("mycommand.staff")) return;
		
		if (args.length < 2) {
			Util.msg(s, "&c/smbc [tag for mute] [broadcast msg]");
		}
		else {
			Util.mutableBroadcast(args[0], SharedUtil.connectArgs(args, 1), false);
		}
	}
}
