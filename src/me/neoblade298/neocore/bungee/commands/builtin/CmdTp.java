package me.neoblade298.neocore.bungee.commands.builtin;
import java.util.ArrayList;
import java.util.Arrays;

import me.neoblade298.neocore.bungee.BungeeCore;
import me.neoblade298.neocore.bungee.util.Util;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class CmdTp extends Command implements TabExecutor {
	private static ArrayList<String> players;
	public CmdTp() {
		super("tp");
	}

	public void execute(CommandSender sender, String[] args) {
		if ((sender instanceof ProxiedPlayer)) {
			ProxiedPlayer src = (ProxiedPlayer) sender;
			if (args.length == 0) {
				src.sendMessage(new ComponentBuilder("Usage: /tp [player]").color(ChatColor.RED).create());
			}
			else {
				ProxiedPlayer trg = ProxyServer.getInstance().getPlayer(args[0]);
				executeTeleport(sender, src, trg);
			}
		}
	}
	
	public static void executeTeleport(CommandSender sender, ProxiedPlayer src, ProxiedPlayer trg) {
		if (!sender.hasPermission("mycommand.staff")) {
			Util.msg(sender, "&cYou don't have permission to do this!");
			return;
		}
		if (trg == null) {
			Util.msg(sender, "&cThis player is not online!");
			return;
		}
		
		if (trg.getServer().getInfo().getName().equals(src.getServer().getInfo().getName())) {
			// Directly tp without connecting
			sendTeleportMsg(src, trg, true);
		}
		else {
			Callback<Boolean> cb = (Boolean success, Throwable throwable) -> {
				if (success) {
					sendTeleportMsg(src, trg, false);
				}
				else {
					ProxyServer.getInstance().getLogger().warning("[NeoCore] Failed to send teleport message for " + src.getName() +
							" to " + trg.getName() + ", connect failed");
				}
			};
			src.connect(trg.getServer().getInfo(), cb);
		}
	}
	
	public static void sendTeleportMsg(ProxiedPlayer src, ProxiedPlayer trg, boolean instant) {
		String[] msgs = new String[] { (instant ? "neocore-tp-instant" : "neocore-tp"),
				src.getUniqueId().toString(), trg.getUniqueId().toString()};
		BungeeCore.sendPluginMessage(Arrays.asList(trg.getServer().getInfo()), msgs, true);
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		return BungeeCore.players;
	}
}
