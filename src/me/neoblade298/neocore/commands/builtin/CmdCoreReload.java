package me.neoblade298.neocore.commands.builtin;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.util.Util;

public class CmdCoreReload implements Subcommand {
	private static final CommandArguments args = new CommandArguments();

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
		return "reload";
	}

	@Override
	public String getDescription() {
		return "Reloads the plugin safely";
	}

	@Override
	public void run(CommandSender s, String[] args) {
		NeoCore.reload();
		Util.msg(s, "&7Successful reload");
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}
}
