package me.neoblade298.neocore.shared.commands;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import me.neoblade298.neocore.bukkit.commands.CmdList;
import net.md_5.bungee.api.ChatColor;

public abstract class AbstractSubcommandManager<T extends AbstractSubcommand> {
	protected TreeMap<String, T> handlers = new TreeMap<String, T>();
	protected HashSet<String> aliases = new HashSet<String>();
	protected String base, perm;
	protected ChatColor color = ChatColor.RED;
	
	public AbstractSubcommandManager(String base, String perm, ChatColor color) {
		this.base = base;
		this.perm = perm;
		this.color = color;
	}
	
	public T parseForCommand(String[] args) {
		// Run command no args
		if (args.length == 0) {
			if (handlers.containsKey("") ) {
				return handlers.get("");
			}
			else {
				return null;
			}
		}
		// Run no-arg command with a number value, specifically for command lists
		else if (StringUtils.isNumeric(args[0]) && handlers.get("") != null && handlers.get("") instanceof CmdList) {
			return handlers.get("");
		}
		// Run command normally
		else if (handlers.containsKey(args[0].toUpperCase())) {
			return handlers.get(args[0].toUpperCase());
		}
		// Run base command with args (like /rename [variable])
		else if (!handlers.containsKey(args[0].toUpperCase()) && handlers.containsKey("")) {
			T cmd = handlers.get("");
			CommandArguments cArgs = cmd.getArgs();
			if ((cArgs.getMin() > 0 || cArgs.getMin() == -1) && (cArgs.getMax() > 0 || cArgs.getMax() == -1)) {
				return cmd;
			}
		}
		return null;
	}
	
	public String[] reduceArgs(String args[], T subcmd) {
		String[] reducedArgs = args;
		
		// Do not reduce args if key is ""
		if (args.length > 0 && subcmd.getKey().length() != 0) {
			reducedArgs = Arrays.copyOfRange(args, 1, args.length);
		}
		return reducedArgs;
	}
	
	public void register(T cmd) {
		handlers.put(cmd.getKey().toUpperCase(), cmd);
		
		if (cmd.getAliases() != null) {
			for (String alias : cmd.getAliases()) {
				aliases.add(alias);
				handlers.put(alias.toUpperCase(), cmd);
			}
		}
	}
	
	public AbstractSubcommand getCommand(String key) {
		return handlers.get(key.toUpperCase());
	}
	
	public Set<String> getKeys() {
		return handlers.keySet();
	}
	
	public String getCommandLine(T sc) {
		String line = (sc.getColor() == null ? "" : sc.getColor()) + "/" + base;
		
		// Add subcommand name
		if (sc.getKey().length() != 0) {
			line += " " + sc.getKey();
		}
		
		// Add args
		line += " " + sc.getArgs().getDisplay();
		
		// Add description
		if (sc.getDescription() != null) {
			line += "§7 - " + sc.getDescription();
		}
		return line;
	}
	
	public String getPermission() {
		return perm;
	}
	
	public String getBase() {
		return base;
	}
	
	public ChatColor getColor() {
		return color;
	}
	
	public HashSet<String> getAliases() {
		return aliases;
	}
}
