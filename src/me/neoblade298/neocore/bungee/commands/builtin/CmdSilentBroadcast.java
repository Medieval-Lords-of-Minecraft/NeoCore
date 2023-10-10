package me.neoblade298.neocore.bungee.commands.builtin;

import com.velocitypowered.api.command.SimpleCommand;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.util.SharedUtil;

public class CmdSilentBroadcast implements SimpleCommand {
	@Override
	public void execute(Invocation inv) {
		if (!inv.source().hasPermission("neocore.staff")) return;
		
		if (inv.arguments().length < 2) {
			Util.msg(inv.source(), "&c/sbc [broadcast msg]");
		}
		else {
			Util.broadcast(SharedUtil.connectArgs(inv.arguments(), 1), false);
		}
	}
}
