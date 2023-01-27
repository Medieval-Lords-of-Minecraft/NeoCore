package me.neoblade298.neocore.bungee.commands.builtin;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class CmdUptime extends Command {
	private long startTime;
	public CmdUptime() {
		super("uptime");
		startTime = System.currentTimeMillis();
	}

	public void execute(CommandSender sender, String[] args) {
		if (sender.hasPermission("mycommand.staff")) {
			long now = System.currentTimeMillis();
			int diff = (int) (now - startTime);
			int days = diff / 86400000;
			diff = diff % 86400000;
			int hours = diff / 3600000;
			diff = diff % 3600000;
			int minutes = diff / 60000;
			diff = diff % 60000;
			int seconds = diff / 1000;
			String uptime = "Uptime: " + days + " days " + hours + " hours " + minutes + " minutes " + seconds + " seconds";
			sender.sendMessage(new TextComponent(uptime));
		}
	}
}
