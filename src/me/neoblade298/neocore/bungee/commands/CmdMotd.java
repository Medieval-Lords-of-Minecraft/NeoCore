package me.neoblade298.neocore.bungee.commands;

import me.neoblade298.neocore.bungee.BungeeCore;
import me.neoblade298.neocore.bungee.util.BUtil;
import me.neoblade298.neocore.util.Util;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class CmdMotd extends Command {
	public CmdMotd() {
		super("motd");
	}

	public void execute(CommandSender sender, String[] args) {
		if (args.length == 0 || !sender.hasPermission("mycommand.staff")) {
			BungeeCore.sendMotd(sender);
		}
		else {
			if (args[0].equalsIgnoreCase("add") && args.length > 1) {
				BungeeCore.addMotd(sender, Util.connectArgs(args, 1));
				BUtil.msg(sender, "&7Successfully added to MOTD");
			}
			else if (args[0].equalsIgnoreCase("remove") && args.length == 2) {
				BungeeCore.removeMotd(sender, Integer.parseInt(args[1]));
				BUtil.msg(sender, "&7Successfully removed from MOTD");
			}
			else {
				BUtil.msg(sender, "&7- /motd add [msg]");
				BUtil.msg(sender, "&7- /motd remove [index from 0]");
			}
		}
	}
}
