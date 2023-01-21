package me.neoblade298.neocore.bukkit.commands.builtin;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.neoblade298.neocore.bukkit.bungee.BungeeAPI;
import me.neoblade298.neocore.bukkit.commands.CommandArgument;
import me.neoblade298.neocore.bukkit.commands.CommandArguments;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.commands.SubcommandRunner;

public class CmdBCoreSend implements Subcommand {
	private static final CommandArguments args = new CommandArguments(Arrays.asList(new CommandArgument("player", false), new CommandArgument("server")));

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
		return "send";
	}

	@Override
	public String getDescription() {
		return "Sends a player or yourself to another server";
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

	@Override
	public CommandArguments getArgs() {
		return args;
	}
}
