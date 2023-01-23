package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.bukkit.commands.CommandArguments;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.commands.SubcommandRunner;
import me.neoblade298.neocore.bukkit.io.IOType;
import me.neoblade298.neocore.bukkit.io.PlayerIOManager;
import me.neoblade298.neocore.shared.util.SharedUtil;

public class CmdIODisabled implements Subcommand {
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
		return "disabled";
	}

	@Override
	public String getDescription() {
		return "Shows any disabled IO";
	}

	@Override
	public void run(CommandSender s, String[] args) {
		for (IOType type : IOType.values()) {
			String msg = "&6" + type + "&7:";
			for (String key : PlayerIOManager.getDisabledIO().get(type)) {
				msg += " &e" + key;
			}
			SharedUtil.msg(s, msg, false);
		}
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}
}
