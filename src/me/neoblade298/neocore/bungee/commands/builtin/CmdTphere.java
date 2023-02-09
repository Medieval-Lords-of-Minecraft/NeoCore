package me.neoblade298.neocore.bungee.commands.builtin;

import me.neoblade298.neocore.bungee.BungeeCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class CmdTphere extends Command implements TabExecutor {
	public CmdTphere() {
		super("tphere");
	}

	public void execute(CommandSender sender, String[] args) {
		if ((sender instanceof ProxiedPlayer)) {
			ProxiedPlayer trg = (ProxiedPlayer) sender;
			if (args.length == 0) {
				trg.sendMessage(new ComponentBuilder("Usage: /tphere [player]").color(ChatColor.RED).create());
			}
			else {
				ProxiedPlayer src = ProxyServer.getInstance().getPlayer(args[0]);
				CmdTp.executeTeleport(sender, src, trg);
			}
		}
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		return BungeeCore.players;
	}
}
