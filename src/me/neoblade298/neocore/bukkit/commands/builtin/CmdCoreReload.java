package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.commands.CommandArguments;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.commands.SubcommandRunner;
import me.neoblade298.neocore.shared.util.SharedUtil;

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
		SharedUtil.msg(s, "&7Successful reload");
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}
}
