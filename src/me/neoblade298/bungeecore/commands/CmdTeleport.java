package me.neoblade298.bungeecore.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CmdTeleport extends Command {
	public CmdTeleport() {
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
				if (trg.getServer().getInfo().getName().equals(src.getServer().getInfo().getName())) {
					ProxyServer.getInstance().getLogger().info("Same server");
					sendTeleportMsg(src, trg);
				}
				else {
					Callback<Boolean> cb = (Boolean success, Throwable throwable) -> {
						if (success) {
							sendTeleportMsg(src, trg);
						}
						else {
							ProxyServer.getInstance().getLogger().warning("[NeoCore] Failed to send teleport message for " + src.getName() +
									" to " + trg.getName() + ", connect failed");
						}
					};
					src.connect(trg.getServer().getInfo(), cb);
					ProxyServer.getInstance().getLogger().info("Different server");
				}
			}
		}
	}
	
	private void sendTeleportMsg(ProxiedPlayer src, ProxiedPlayer trg) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("neocore-tp");
		out.writeUTF(src.getUniqueId().toString());
		out.writeUTF(trg.getUniqueId().toString());
		trg.getServer().getInfo().sendData("neocore:bungee", out.toByteArray());
		ProxyServer.getInstance().getLogger().info("Sent msg to " + trg.getServer().getInfo() + ", byte array length " + out.toByteArray().length);
	}
}
