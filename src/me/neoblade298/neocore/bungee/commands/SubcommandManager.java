package me.neoblade298.neocore.bungee.commands;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import me.neoblade298.neocore.bungee.BungeeCore;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.CommandArguments;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.TabExecutor;

public class SubcommandManager extends Command implements TabExecutor {
	private CommandOverhead overhead;
	public SubcommandManager(String base, String perm, ChatColor color, Plugin plugin) {
		this(base, perm, color, plugin, new String[0]);
	}
	public SubcommandManager(String base, String perm, ChatColor color, Plugin plugin, String[] aliases) {
		super(base, null, aliases);
		overhead = new CommandOverhead(base, perm, color);
		plugin.getProxy().getPluginManager().registerCommand(plugin, this);
	}
	
	@Override
	public void execute(CommandSender s, String[] args) {
		Subcommand sc = overhead.parseForCommand(args);
		if (sc == null) return;
		
		args = overhead.reduceArgs(args, sc);
		if (check(sc, s, args)) {
			sc.run(s, args);
		}
	}
	
	private boolean check(Subcommand cmd, CommandSender s, boolean silent) {
		// If cmd permission exists, it overrides list permission
		String activePerm = cmd.getPermission() != null ? cmd.getPermission() : overhead.getPermission();
		
		if (activePerm != null && !s.hasPermission(activePerm)) {
			if (!silent) Util.msg(s, "&cYou're missing the permission: " + activePerm);
			return false;
		}

		if ((cmd.getRunner() == SubcommandRunner.PLAYER_ONLY && !(s instanceof ProxiedPlayer)) ||
				(cmd.getRunner() == SubcommandRunner.CONSOLE_ONLY && !(s == BungeeCore.inst().getProxy().getConsole()))) {
			if (!silent) Util.msg(s, "&cYou are the wrong type of user for this command!");
			return false;
		}
		return true;
	}
	
	private boolean check(Subcommand cmd, CommandSender s, String[] args) {
		if (!check(cmd, s, false)) return false;
		
		CommandArguments cargs = cmd.getArgs();
		if (args.length < cargs.getMin() && cargs.getMin() != -1) {
			Util.msg(s, "&cThis command requires at least " + cargs.getMin() + " args but received " + args.length + ".");
			Util.msg(s, "&c" + overhead.getCommandLine(cmd));
			return false;
		}

		if (args.length > cargs.getMax() && cargs.getMax() != -1) {
			Util.msg(s, "&cThis command requires at most " + cargs.getMax() + " args but received " + args.length + ".");
			Util.msg(s, "&c" + overhead.getCommandLine(cmd));
			return false;
		}
		
		return true;
	}
	
	public void registerCommandList(String key, String perm, ChatColor color) {
		overhead.getHandlers().put(key.toLowerCase(), new CmdList(key, overhead.getBase(), perm, overhead.getPermission(), overhead.getHandlers(), overhead.getAliases(), overhead.getColor(), color));
	}
	
	public void registerCommandList(String key) {
		overhead.getHandlers().put(key.toLowerCase(),
				new CmdList(key, overhead.getBase(), null, overhead.getPermission(), overhead.getHandlers(), overhead.getAliases(), overhead.getColor()));
	}
	
	public void register(Subcommand cmd) {
		overhead.register(cmd);
	}
	
	public Subcommand getCommand(String key) {
		return overhead.getHandlers().get(key.toLowerCase());
	}
	
	public Set<String> getKeys() {
		return overhead.getHandlers().keySet();
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender s, String[] args) {
		if (!(s instanceof ProxiedPlayer)) return Collections.emptyList(); // Only player senders can use tab complete
		
		if (overhead.getPermission() != null && !s.hasPermission(overhead.getPermission())) return Collections.emptyList();
		
		if (args.length == 1) {
			// Get all commands that can be run by user
			return overhead.getHandlers().values().stream()
					.filter(cmd -> check(cmd, s, true) && !cmd.isHidden() && cmd.getKey().length() > 0)
					.map(cmd -> cmd.getKey())
					.toList();
		}
		else {
			if (args[0].isBlank() || StringUtils.isNumeric(args[0])) return Collections.emptyList();
			
			// Only look for a subcommand if the first arg is not a number and not blank
			Subcommand cmd = overhead.getHandlers().get(args[0].toLowerCase());
			if (cmd == null || cmd.isHidden() || !cmd.isTabEnabled()) return Collections.emptyList();
			
			CommandArguments ca = cmd.getArgs();
			Arg arg = CommandArguments.getCurrentArg(args, ca);
			return arg.getTabOptions() != null ? arg.getTabOptions() : Collections.emptyList();
		}
	}
}
