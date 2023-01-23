package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.bukkit.commands.CommandArguments;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.commands.SubcommandRunner;
import me.neoblade298.neocore.bukkit.io.IOComponentWrapper;
import me.neoblade298.neocore.bukkit.io.PlayerIOManager;
import me.neoblade298.neocore.bukkit.util.BukkitUtil;

public class CmdIOList implements Subcommand {
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
		return "list";
	}

	@Override
	public String getDescription() {
		return "Lists IO Components by order of priority";
	}

	@Override
	public void run(CommandSender s, String[] args) {
		for (IOComponentWrapper io : PlayerIOManager.getComponents()) {
			BukkitUtil.msg(s, "&7- &6" + io.getKey() + " (&e" + io.getPriority() + "&6)", false);
		}
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}
}
