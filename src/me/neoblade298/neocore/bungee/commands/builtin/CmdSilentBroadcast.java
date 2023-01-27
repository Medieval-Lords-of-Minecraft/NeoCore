package me.neoblade298.neocore.bungee.commands.builtin;

import me.neoblade298.neocore.bungee.BungeeCore;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.util.SharedUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class CmdSilentBroadcast extends Command {
	public CmdSilentBroadcast() {
		super("sbc");
	}

	@Override
	public void execute(CommandSender s, String[] args) {
		if (args.length == 0) {
			Util.msg(s, "&c/sbc [broadcast msg]");
		}
		else {
			BungeeCore.inst().getProxy().broadcast(new TextComponent(SharedUtil.translateColors(SharedUtil.connectArgs(args))));
		}
	}
}
