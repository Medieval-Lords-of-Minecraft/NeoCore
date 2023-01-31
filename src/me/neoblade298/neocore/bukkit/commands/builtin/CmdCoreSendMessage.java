package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.messaging.MessagingManager;
import me.neoblade298.neocore.bukkit.util.Util;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class CmdCoreSendMessage extends Subcommand {
	public CmdCoreSendMessage(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("player", false), new Arg("message"), new Arg("page", false));
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
		if (args.length - offset == 1) {
			parseAndRun(s, recipient, args[offset]);
		}
		// message and page
		else if (args.length - offset == 2) {
			parseAndRun(s, recipient, args[offset], Integer.parseInt(args[offset + 1]));
		}
	}
	
	public static void parseAndRun(CommandSender s, CommandSender recipient, String key) {
		MessagingManager.sendMessage(s, recipient, key);
	}
	
	public static void parseAndRun(CommandSender s, CommandSender recipient, String key, int page) {
		MessagingManager.sendMessage(s, recipient, key, page);
		if (s != recipient) {
			Util.msg(s, "&7Successfully sent message");
		}
	}
}
