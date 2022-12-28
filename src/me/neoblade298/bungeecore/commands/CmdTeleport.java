package me.neoblade298.bungeecore.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CmdTeleport extends Command {
	public CmdTeleport() {
		super("tp");
	}

	public void execute(CommandSender sender, String[] args) {
		if ((sender instanceof ProxiedPlayer)) {

		}
	}
}
