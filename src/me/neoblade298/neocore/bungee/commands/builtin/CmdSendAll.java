package me.neoblade298.neocore.bungee.commands.builtin;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;

import me.neoblade298.neocore.bungee.BungeeCore;
import me.neoblade298.neocore.bungee.util.Util;

public class CmdSendAll implements SimpleCommand {
	@Override
	public void execute(Invocation inv) {
		if (!inv.source().hasPermission("mycommand.staff")) return;
		String[] args = inv.arguments();
		if (args.length != 2) {
			Util.msg(inv.source(), "&cNot enough arguments! /sendall [server-from] [server-to]");
			return;
		}
		for (Player p : BungeeCore.proxy().getServer(args[0]).get().getPlayersConnected()) {
			p.createConnectionRequest(BungeeCore.proxy().getServer(args[1]).get());
		}
	}
}
