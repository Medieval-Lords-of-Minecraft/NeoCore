package me.neoblade298.neocore.bungee.commands.builtin;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;

import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.util.SharedUtil;

public class CmdBroadcast implements SimpleCommand {

	@Override
	public void execute(Invocation inv) {
		if (!inv.source().hasPermission("neocore.staff")) return;
		
		if (inv.arguments().length == 0) {
			Util.msg(inv.source(), "&c/bc [broadcast msg]");
		}
		else {
			Util.broadcast(SharedUtil.connectArgs(inv.arguments()));
		}
	}
	
	public static CommandMeta meta(CommandManager mngr, Object plugin) {
        CommandMeta meta = mngr.metaBuilder("bc")
            .plugin(plugin)
            .build();
        
        return meta;
	}
}
