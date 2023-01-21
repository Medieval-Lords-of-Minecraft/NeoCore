package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.bukkit.commands.CommandArguments;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.commands.SubcommandRunner;
import me.neoblade298.neocore.bukkit.scheduler.SchedulerAPI;

public class CmdCoreSchedule implements Subcommand {
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
		return "schedule";
	}

	@Override
	public String getDescription() {
		return "Lists all items in the current scheduler";
	}

	@Override
	public void run(CommandSender s, String[] args) {
		SchedulerAPI.display(s);
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}
}
