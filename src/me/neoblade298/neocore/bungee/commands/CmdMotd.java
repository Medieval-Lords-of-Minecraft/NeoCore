package me.neoblade298.neocore.bungee.commands;

import me.neoblade298.neocore.bungee.BungeeCore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CmdMotd extends Command {
	public CmdMotd() {
		super("motd");
	}

	public void execute(CommandSender sender, String[] args) {
		BungeeCore.sendMotd(sender);
	}
}
