package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.neoblade298.neocore.bukkit.bungee.BungeeAPI;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class CmdBCoreSend extends Subcommand {
	public CmdBCoreSend(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("player", false), new Arg("server"));
	}

	@Override
	public void run(CommandSender s, String[] args) {
		if (args.length == 1 && s instanceof Player) {
			BungeeAPI.sendPlayer((Player) s, args[0]);
		}
		else if (args.length == 2) {
			BungeeAPI.sendPlayer(Bukkit.getPlayer(args[0]), args[1]);
		}
	}
}