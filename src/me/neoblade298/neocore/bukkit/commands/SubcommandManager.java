package me.neoblade298.neocore.bukkit.commands;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.neoblade298.neocore.shared.commands.AbstractSubcommand;
import me.neoblade298.neocore.shared.commands.AbstractSubcommandManager;
import me.neoblade298.neocore.shared.commands.CommandArguments;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import net.kyori.adventure.text.format.TextColor;

public class SubcommandManager extends AbstractSubcommandManager<Subcommand> implements CommandExecutor, TabCompleter {
	private static final String MSG_INVALID = "§cInvalid command! Are you using the right syntax? /";
	private static final String MSG_MISSING_PERM = "§cYou're missing the permission: ";
	private static final String MSG_WRONG_USER = "§cYou are the wrong type of user for this command!";

	private static String argCountMsg(String qualifier, int count, int received) {
		return "§cThis command requires " + qualifier + " " + count + " args but received " + received + ".";
	}

	public SubcommandManager(String base, String perm, TextColor color, JavaPlugin plugin) {
		super(base, perm, color);
		plugin.getCommand(base).setExecutor(this);
		plugin.getCommand(base).setTabCompleter(this);
	}
	
	// Nested use (e.g. SubcommandGroup): routes but does not register a top-level
	// Bukkit command. base should be the full path so far (e.g. "cmd subcmd1").
	SubcommandManager(String base, String perm, TextColor color) {
		super(base, perm, color);
	}
	
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String lbl, String[] args) {
		dispatch(s, args);
		return true;
	}
	
	// Routes args to a subcommand. Returns true only if a valid command was found,
	// passed all checks, and was executed. Enables recursive/nested dispatch.
	public boolean dispatch(CommandSender s, String[] args) {
		Subcommand sc = super.parseForCommand(args);
		if (sc == null) {
			s.sendMessage(MSG_INVALID + base);
			return false;
		}
		
		args = super.reduceArgs(args, sc);
		if (check(sc, s, args)) {
			sc.run(s, args);
			return true;
		}
		return false;
	}
	
	
	// Modular for tabcomplete to use
	private boolean check(Subcommand cmd, CommandSender s, boolean silent) {
		// If cmd permission exists, it overrides list permission
		String activePerm = cmd.getPermission() != null ? cmd.getPermission() : perm;
		if (activePerm != null && !s.hasPermission(activePerm)) {
			if (!silent) s.sendMessage(MSG_MISSING_PERM + activePerm);
			return false;
		}

		if ((cmd.getRunner() == SubcommandRunner.PLAYER_ONLY && !(s instanceof Player)) ||
				(cmd.getRunner() == SubcommandRunner.CONSOLE_ONLY && !(s instanceof ConsoleCommandSender))) {
			if (!silent) s.sendMessage(MSG_WRONG_USER);
			return false;
		}
		return true;
	}
	
	private boolean check(Subcommand cmd, CommandSender s, String[] args) {
		if (!check(cmd, s, false)) return false;
		
		CommandArguments cargs = cmd.getArgs();
		if (args.length < cargs.getMin() && cargs.getMin() != -1) {
			s.sendMessage(argCountMsg("at least", cargs.getMin(), args.length));
			s.sendMessage(getCommandLine(cmd));
			return false;
		}
		
		if (args.length > cargs.getMax() && cargs.getMax() != -1) {
			s.sendMessage(argCountMsg("at most", cargs.getMax(), args.length));
			s.sendMessage(getCommandLine(cmd));
			return false;
		}
		
		return true;
	}
	
	public void registerCommandList(String key) {
		registerCommandList(key, null, null);
	}
	
	public void registerCommandList(String key, String perm, TextColor color) {
		handlers.put(key.toLowerCase(), new CmdList(key, base, perm, super.perm, handlers, aliases, this.color, color));
	}
	
	public AbstractSubcommand getCommand(String key) {
		return handlers.get(key.toLowerCase());
	}
	
	public Set<String> getKeys() {
		return handlers.keySet();
	}

	@Override
	public List<String> onTabComplete(CommandSender s, Command command, String label, String[] args) {
		return tabComplete(s, args);
	}
	
	// Resolves tab completions for the given args. Split out from onTabComplete so a
	// nested SubcommandGroup can delegate into a child manager recursively.
	public List<String> tabComplete(CommandSender s, String[] args) {
		if (!(s instanceof Player)) return null;
		
		if (perm != null && !s.hasPermission(perm)) return null;
		
		if (args.length == 1) {
			// Get all commands that can be run by user
			return handlers.values().stream()
					.filter(cmd -> check(cmd, s, true) && !cmd.isHidden() && cmd.getKey().length() > 0 && cmd.getKey().toLowerCase().startsWith(args[0].toLowerCase()))
					.map(cmd -> cmd.getKey())
					.toList();
		}
		else {
			if (args[0].isBlank() || StringUtils.isNumeric(args[0])) return null;
			
			// Only look for a subcommand if the first arg is not a number and not blank
			Subcommand cmd = handlers.get(args[0].toLowerCase());
			if (cmd == null || cmd.isHidden() || !cmd.isTabEnabled()) return null;
			
			if (cmd.overridesTab) {
				return cmd.getTabOptions(s, args);
			}
			
			CommandArguments ca = cmd.getArgs();
			return ca.getTabCompletions(args);
		}
	}
}
