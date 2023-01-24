package me.neoblade298.neocore.bukkit.commands.builtin;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.bukkit.commands.CommandArguments;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.commands.SubcommandRunner;
import me.neoblade298.neocore.shared.io.ConnectionDetails;
import me.neoblade298.neocore.shared.io.SQLManager;

public class CmdIOFindLeaks implements Subcommand {
	private static final CommandArguments args = new CommandArguments();

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
		return "findleaks";
	}

	@Override
	public String getDescription() {
		return "Look for unclosed connections in last 100 connections";
	}

	@Override
	public void run(CommandSender s, String[] args) {
		boolean found = false;
		for (ConnectionDetails details : SQLManager.getLatestConnections()) {
			try {
				if (!details.getConnection().isClosed()) {
					found = true;
					Bukkit.getLogger().warning("[NeoCore] Unclosed connection found by user " + details.getUser() + " created at time " + details.getTimeCreated() + ". Current time: " + System.currentTimeMillis());
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (!found) {
			Bukkit.getLogger().info("[NeoCore] No connection leaks found in the past 100 connections.");
		}
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}
}
