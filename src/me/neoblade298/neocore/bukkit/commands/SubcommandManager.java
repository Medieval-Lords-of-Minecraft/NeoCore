package me.neoblade298.neocore.bukkit.commands;

import java.util.Set;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


import me.neoblade298.neocore.shared.commands.AbstractSubcommandManager;
import me.neoblade298.neocore.shared.commands.CommandArguments;
import me.neoblade298.neocore.shared.commands.AbstractSubcommand;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import net.md_5.bungee.api.ChatColor;

public class SubcommandManager extends AbstractSubcommandManager<Subcommand> implements CommandExecutor  {
	public SubcommandManager(String base, String perm, ChatColor color, JavaPlugin plugin) {
		super(base, perm, color);
		plugin.getCommand(base).setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String lbl, String[] args) {
		Subcommand sc = super.parseForCommand(args);
		if (sc == null) return false;
		
		args = super.reduceArgs(args, sc);
		if (check(sc, s, args)) {
			sc.run(s, args);
		}
		return true;
	}
	
	private boolean check(Subcommand cmd, CommandSender s, String[] args) {
		// If cmd permission exists, it overrides list permission
		String activePerm = cmd.getPermission() != null ? cmd.getPermission() : perm;
		if (activePerm != null && !s.hasPermission(activePerm)) {
			s.sendMessage("§cYou're missing the permission: " + activePerm);
			return false;
		}

		if ((cmd.getRunner() == SubcommandRunner.PLAYER_ONLY && !(s instanceof Player)) ||
				(cmd.getRunner() == SubcommandRunner.CONSOLE_ONLY && !(s instanceof ConsoleCommandSender))) {
			s.sendMessage("§cYou are the wrong type of user for this command!");
			return false;
		}
		
		CommandArguments cargs = cmd.getArgs();
		if (args.length < cargs.getMin() && cargs.getMin() != -1) {
			s.sendMessage("§cThis command requires at least " + cargs.getMin() + " args but received " + args.length + ".");
			s.sendMessage("§c" + getCommandLine(cmd));
			return false;
		}
		
		if (args.length > cargs.getMax() && cargs.getMax() != -1) {
			s.sendMessage("§cThis command requires at most " + cargs.getMax() + " args but received " + args.length + ".");
			s.sendMessage("§c" + getCommandLine(cmd));
			return false;
		}
		
		return true;
	}
	
	public void registerCommandList(String key) {
		registerCommandList(key, null, null);
	}
	
	public void registerCommandList(String key, String perm, ChatColor color) {
		handlers.put(key.toUpperCase(), new CmdList(key, base, perm, handlers, aliases, this.color, color));
	}
	
	public AbstractSubcommand getCommand(String key) {
		return handlers.get(key.toUpperCase());
	}
	
	public Set<String> getKeys() {
		return handlers.keySet();
	}
}
