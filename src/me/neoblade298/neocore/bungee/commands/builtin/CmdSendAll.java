package me.neoblade298.neocore.bungee.commands.builtin;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;

import me.neoblade298.neocore.bungee.BungeeCore;
import me.neoblade298.neocore.bungee.util.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CmdSendAll implements SimpleCommand {
	private static final Component usage = Component.text("Not enough arguments! /sendall [server-from] [server-to]", NamedTextColor.RED);
	public static CommandMeta meta(CommandManager mngr, Object plugin) {
        CommandMeta meta = mngr.metaBuilder("sendall")
            .plugin(plugin)
            .build();
        
        return meta;
	}
	
	@Override
	public void execute(Invocation inv) {
		String[] args = inv.arguments();
		if (args.length != 2) {
			Util.msg(inv.source(), usage);
			return;
		}
		for (Player p : BungeeCore.proxy().getServer(args[0]).get().getPlayersConnected()) {
			p.createConnectionRequest(BungeeCore.proxy().getServer(args[1]).get()).fireAndForget();
		}
	}
	
	@Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("neocore.staff");
    }

}
