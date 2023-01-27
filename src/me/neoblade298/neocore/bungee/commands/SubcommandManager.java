package me.neoblade298.neocore.bungee.commands;

import java.util.Set;

import me.neoblade298.neocore.shared.commands.CommandArguments;
import me.neoblade298.neocore.bungee.BungeeCore;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

public class SubcommandManager extends Command {
	private CommandOverhead overhead;
	public SubcommandManager(String base, String perm, ChatColor color, Plugin plugin) {
		super(base);
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
	
	private boolean check(Subcommand cmd, CommandSender s, String[] args) {
		// If cmd permission exists, it overrides list permission
		String activePerm = cmd.getPermission() != null ? cmd.getPermission() : overhead.getPermission();
		
		if (activePerm != null && !s.hasPermission(activePerm)) {
			Util.msg(s, "&cYou're missing the permission: " + activePerm);
			return false;
		}

		if ((cmd.getRunner() == SubcommandRunner.PLAYER_ONLY && !(s instanceof ProxiedPlayer)) ||
				(cmd.getRunner() == SubcommandRunner.CONSOLE_ONLY && !(s == BungeeCore.inst().getProxy().getConsole()))) {
			Util.msg(s, "&cYou are the wrong type of user for this command!");
			return false;
		}
		
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
		overhead.getHandlers().put(key.toUpperCase(), new CmdList(key, overhead.getBase(), perm, overhead.getHandlers(), overhead.getAliases(), overhead.getColor(), color));
	}
	
	public Subcommand getCommand(String key) {
		return overhead.getHandlers().get(key.toUpperCase());
	}
	
	public Set<String> getKeys() {
		return overhead.getHandlers().keySet();
	}
}
