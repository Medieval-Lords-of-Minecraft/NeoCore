package me.neoblade298.neocore.commands.builtin;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.util.Util;

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
			Util.msg(s, "&7Successfully enabled debug mode!");
		}
		else {
			Util.msg(s, "&7Successfully disabled debug mode!");
		}
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}
}
