package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.commands.CommandArguments;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.commands.SubcommandRunner;
import me.neoblade298.neocore.bukkit.util.BukkitUtil;

public class CmdCoreDebug implements Subcommand {
	private static final CommandArguments args = new CommandArguments();

	@Override
	public String getPermission() {
		return "neocore.admin";
	}

	@Override
	public SubcommandRunner getRunner() {
		return SubcommandRunner.BOTH;
	}

	@Override
	public String getKey() {
		return "debug";
	}

	@Override
	public String getDescription() {
		return "Toggles debug mode, currently just for PlayerFields/PlayerTags";
	}

	@Override
	public void run(CommandSender s, String[] args) {
		if (NeoCore.toggleDebug()) {
			BukkitUtil.msg(s, "&7Successfully enabled debug mode!");
		}
		else {
			BukkitUtil.msg(s, "&7Successfully disabled debug mode!");
		}
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}
}
