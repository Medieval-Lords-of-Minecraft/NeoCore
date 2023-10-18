package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.io.IOType;
import me.neoblade298.neocore.bukkit.io.PlayerIOManager;
import me.neoblade298.neocore.bukkit.util.Util;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CmdIOEnable extends Subcommand {
	public CmdIOEnable(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("action", true), new Arg("io key", false));
	}

	@Override
	public void run(CommandSender s, String[] args) {
		IOType type = IOType.valueOf(args[0].toUpperCase());
		if (args.length == 1) {
			PlayerIOManager.enableIO(type);
			Util.msg(s, Component.text("Successfully set " + type + " to enabled.", NamedTextColor.GRAY));
		}
		else {
			PlayerIOManager.enableIO(type, args[1]);
			Util.msg(s, Component.text("Successfully set " + type + " to enabled for manager " + args[1] + ".", NamedTextColor.GRAY));
		}
	}
}
