package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.util.BukkitUtil;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neocore.shared.util.SharedUtil;

public class CmdCoreRawMessage extends Subcommand {

	public CmdCoreRawMessage(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.setOverride("{player} [msg]");
		args.setMin(1);
	}

	@Override
	public void run(CommandSender s, String[] args) {
		parseAndRun(s, args);
	}
	
	public static void parseAndRun(CommandSender s, String[] args) {
		int offset = 0;
		CommandSender recipient = s;
		if (Bukkit.getPlayer(args[0]) != null) {
			recipient = Bukkit.getPlayer(args[0]);
			offset = 1;
		}
		
		// message only
		if (offset == 0) {
			BukkitUtil.msg(recipient, SharedUtil.connectArgs(args), false);
		}
		// message and page
		else if (offset == 1) {
			BukkitUtil.msg(recipient, SharedUtil.connectArgs(args, 1), false);
		}
	}
}
