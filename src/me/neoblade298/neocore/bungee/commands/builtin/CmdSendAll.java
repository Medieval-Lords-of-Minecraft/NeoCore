package me.neoblade298.neocore.bungee.commands.builtin;

import me.neoblade298.neocore.bungee.util.Util;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CmdSendAll extends Command {
	public CmdSendAll() {
		super("sendall");
	}

	public void execute(CommandSender sender, String[] args) {
		if (!sender.hasPermission("mycommand.staff")) return;
		if (args.length != 2) {
			Util.msg(sender, "&cNot enough arguments! /sendall [server-from] [server-to]");
			return;
		}
		for (ProxiedPlayer p : ProxyServer.getInstance().getServerInfo(args[0]).getPlayers()) {
			p.connect(ProxyServer.getInstance().getServerInfo(args[1]));
		}
	}
}
