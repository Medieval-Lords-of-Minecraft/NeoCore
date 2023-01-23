package me.neoblade298.neocore.bukkit.commands.builtin;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.bukkit.commands.CommandArgument;
import me.neoblade298.neocore.bukkit.commands.CommandArguments;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.commands.SubcommandRunner;
import me.neoblade298.neocore.shared.messaging.MessagingManager;
import me.neoblade298.neocore.shared.util.SharedUtil;

public class CmdCoreSendMessage implements Subcommand {
	private static final CommandArguments args = new CommandArguments(Arrays.asList(
			new CommandArgument("player", false), new CommandArgument("message"), new CommandArgument("page", false)));

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
		return "sendmsg";
	}

	@Override
	public String getDescription() {
		return "Plays a message";
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
			SharedUtil.msg(s, "&7Successfully sent message");
		}
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}
}