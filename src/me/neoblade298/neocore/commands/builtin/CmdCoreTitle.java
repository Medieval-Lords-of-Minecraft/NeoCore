package me.neoblade298.neocore.commands.builtin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.util.Util;

public class CmdCoreTitle implements Subcommand {

	@Override
	public String getPermission() {
		return "mycommand.staff";
	}

	@Override
	public SubcommandRunner getRunner() {
		return SubcommandRunner.BOTH;
	}

	@Override
	public String getKey() {
		return "title";
	}

	@Override
	public String getDescription() {
		return "Sends a title to a player";
	}

	@Override
	public void run(CommandSender s, String[] args) {
		Player p = Bukkit.getPlayer(args[0]);
		String title = "";
		String subtitle = "";
		int fadeIn = 20;
		int stay = 60;
		int fadeOut = 20;
		
		for (int i = 1; i < args.length; i++) {
			String arg = args[i];
			if (arg.equalsIgnoreCase("--title")) {
				int size = 0;
				for (int j = i + 1; j < args.length; j++) {
					if (args[j].startsWith("--")) {
						i = j - 1; // Since i iterates after this
						break;
					}
					else {
						if (size > 0) title += " ";
						title += args[j];
						size++;
					}
				}
			}
			else if (arg.equalsIgnoreCase("--subtitle")) {
				int size = 0;
				for (int j = i + 1; j < args.length; j++) {
					if (args[j].startsWith("--")) {
						i = j - 1; // Since i iterates after this
						break;
					}
					else {
						if (size > 0) subtitle += " ";
						subtitle += args[j];
						size++;
					}
				}
			}
			else if (arg.equalsIgnoreCase("--in")) {
				fadeIn = Integer.parseInt(args[++i]);
			}
			else if (arg.equalsIgnoreCase("--stay")) {
				stay = Integer.parseInt(args[++i]);
			}
			else if (arg.equalsIgnoreCase("--out")) {
				fadeOut = Integer.parseInt(args[++i]);
			}
		}
		
		p.sendTitle(Util.translateColors(title), Util.translateColors(subtitle), fadeIn, stay, fadeOut);
	}

	@Override
	public String getArgOverride() {
		return "[player] {--title x} {--subtitle x} {--in x} {--stay x} {--out x}";
	}
}
