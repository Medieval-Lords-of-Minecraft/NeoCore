package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class CmdCorePlayerMessage extends Subcommand {
	public CmdCorePlayerMessage(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("message"), new Arg("page", false));
		hidden = true;
	}

	@Override
	public void run(CommandSender s, String[] args) {
		CmdCoreSendMessage.parseAndRun(s, args);
	}
}
