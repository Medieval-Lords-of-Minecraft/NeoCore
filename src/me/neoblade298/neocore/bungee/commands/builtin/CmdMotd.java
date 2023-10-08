package me.neoblade298.neocore.bungee.commands.builtin;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;

import me.neoblade298.neocore.bungee.BungeeCore;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.util.SharedUtil;

public class CmdMotd implements SimpleCommand {
	public void execute(Invocation inv) {
		CommandSource src = inv.source();
		String[] args = inv.arguments();
		if (args.length == 0 || !src.hasPermission("neocore.staff")) {
			BungeeCore.sendMotd(src);
		}
		else {
			if (args[0].equalsIgnoreCase("add") && args.length > 1) {
				BungeeCore.addMotd(src, SharedUtil.connectArgs(args, 1));
				Util.msg(src, "&7Successfully added to MOTD");
			}
			else if (args[0].equalsIgnoreCase("remove") && args.length == 2) {
				BungeeCore.removeMotd(src, Integer.parseInt(args[1]));
				Util.msg(src, "&7Successfully removed from MOTD");
			}
			else {
				Util.msg(src, "&7- /motd add [msg]");
				Util.msg(src, "&7- /motd remove [index from 0]");
			}
		}
	}
	
	public static CommandMeta meta(CommandManager mngr, Object plugin) {
        CommandMeta meta = mngr.metaBuilder("motd")
            .plugin(plugin)
            .build();
        
        return meta;
	}
}
