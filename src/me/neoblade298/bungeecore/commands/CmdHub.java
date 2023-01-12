package me.neoblade298.bungeecore.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CmdHub extends Command {
	public CmdHub() {
		super("hub");
	}

	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) return;
		ProxiedPlayer p = (ProxiedPlayer) sender;
		p.connect(ProxyServer.getInstance().getServerInfo("Hub"));
	}
}
