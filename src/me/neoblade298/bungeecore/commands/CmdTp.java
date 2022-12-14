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

public class CmdTp extends Command {
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
			BUtil.msg(sender, "&cYou don't have permission to do this!");
			return;
		}
		if (trg == null) {
			BUtil.msg(sender, "&cThis player is not online!");
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
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(instant ? "neocore-tp-instant" : "neocore-tp");
		out.writeUTF(src.getUniqueId().toString());
		out.writeUTF(trg.getUniqueId().toString());
		trg.getServer().getInfo().sendData("neocore:bungee", out.toByteArray());
	}
}
