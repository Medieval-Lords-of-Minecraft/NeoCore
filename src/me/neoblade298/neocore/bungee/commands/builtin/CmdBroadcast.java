package me.neoblade298.neocore.bungee.commands.builtin;

import me.neoblade298.neocore.bungee.BungeeCore;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.util.SharedUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class CmdBroadcast extends Command {
	public CmdBroadcast() {
		super("bc");
	}

	@Override
	public void execute(CommandSender s, String[] args) {
		if (args.length == 0) {
			Util.msg(s, "&c/bc [broadcast msg]");
		}
		else {
			BungeeCore.inst().getProxy().broadcast(new TextComponent("&4[&c&lMLMC&4] " + SharedUtil.translateColors(SharedUtil.connectArgs(args))));
		}
	}
}
