package me.neoblade298.neocore.commands.builtin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.neoblade298.neocore.bungee.BungeeAPI;
import me.neoblade298.neocore.commands.CommandArgument;
import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.io.IOComponentWrapper;
import me.neoblade298.neocore.io.IOManager;
import me.neoblade298.neocore.util.Util;

public class CmdTp implements Subcommand {
	private static final CommandArguments args = new CommandArguments(new CommandArgument("player"));

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public SubcommandRunner getRunner() {
		return SubcommandRunner.BOTH;
	}

	@Override
	public String getKey() {
		return "tp";
	}

	@Override
	public String getDescription() {
		return "Teleports to a player cross-server";
	}

	@Override
	public void run(CommandSender s, String[] args) {
		if (!(s instanceof Player)) return;
		Player src = (Player) s;
		Player trg = Bukkit.getPlayer(args[0]);
		if (Bukkit.getPlayer(args[0]) != null) {
			Util.msg(src, "&7Successfully teleported to &e" + trg.getName() + "&7!");
			src.teleport(trg);
		}
		else {
			// Try to teleport via bungee
			BungeeAPI.sendPluginMessage("BungeeCord", "neocore-tp", new String[] { src.getUniqueId().toString(), trg.getUniqueId().toString() });
		}
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}
}
