package me.neoblade298.bungeecore.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.neoblade298.bungeecore.util.BUtil;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CmdTphere extends Command {
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
}
