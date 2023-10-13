package me.neoblade298.neocore.bungee.commands.builtin;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;

import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.util.SharedUtil;

public class CmdMutableBroadcast implements SimpleCommand {
	@Override
	public void execute(Invocation inv) {
		if (inv.arguments().length < 2) {
			Util.msg(inv.source(), "&c/mbc [tag for mute] [broadcast msg]");
		}
		else {
			Util.mutableBroadcast(inv.arguments()[0], SharedUtil.connectArgs(inv.arguments(), 1));
		}
	}
	
    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("neocore.staff");
    }
    
	public static CommandMeta meta(CommandManager mngr, Object plugin) {
        CommandMeta meta = mngr.metaBuilder("mbc")
            .plugin(plugin)
            .build();
        return meta;
	}
}
