package me.neoblade298.neocore.bungee.commands.builtin;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;

import me.neoblade298.neocore.bungee.BungeeCore;

public class CmdHub implements SimpleCommand {
	
	public void execute(Invocation inv) {
		if (!inv.source().hasPermission("neocore.staff")) return;
		if (inv.source() instanceof ConsoleCommandSource) return;

		Player p = (Player) inv.source();
		p.createConnectionRequest(BungeeCore.getProxy().getServer("hub").get());
	}
	
	public static CommandMeta meta(CommandManager mngr, Object plugin) {
        CommandMeta meta = mngr.metaBuilder("hub")
            .plugin(plugin)
            .build();
        
        return meta;
	}
}
