package me.neoblade298.neocore.bungee.commands.builtin;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CmdKickAll extends Command {
	public CmdKickAll() {
		super("kickall");
	}

	public void execute(CommandSender sender, String[] args) {
		if (!sender.hasPermission("mycommand.staff")) return;
		for (ProxiedPlayer p : ProxyServer.getInstance().getServerInfo(args[0]).getPlayers()) {
			p.disconnect(new TextComponent("§cServer is going down for maintenance!"));
		}
	}
}
