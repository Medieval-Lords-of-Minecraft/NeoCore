package me.neoblade298.neocore.shared.commands;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import me.neoblade298.neocore.bukkit.commands.CmdList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public abstract class AbstractSubcommandManager<T extends AbstractSubcommand> {
	protected TreeMap<String, T> handlers = new TreeMap<String, T>();
	protected HashSet<String> aliases = new HashSet<String>();
	protected String base, perm;
	protected TextColor color = NamedTextColor.RED;
	
	public AbstractSubcommandManager(String base, String perm, TextColor color) {
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
		else if (handlers.containsKey(args[0].toLowerCase())) {
			return handlers.get(args[0].toLowerCase());
		}
		// Run base command with args (like /rename [variable])
		else if (!handlers.containsKey(args[0].toLowerCase()) && handlers.containsKey("")) {
			T cmd = handlers.get("");
			CommandArguments cArgs = cmd.getArgs();
			if ((cArgs.getMin() <= args.length || cArgs.getMin() == -1) && (cArgs.getMax() > 0 || cArgs.getMax() == -1)) {
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
		handlers.put(cmd.getKey().toLowerCase(), cmd);
		
		if (cmd.getAliases() != null) {
			for (String alias : cmd.getAliases()) {
				aliases.add(alias.toLowerCase());
				handlers.put(alias.toLowerCase(), cmd);
			}
		}
	}
	
	public AbstractSubcommand getCommand(String key) {
		return handlers.get(key.toUpperCase());
	}
	
	public Set<String> getKeys() {
		return handlers.keySet();
	}
	
	public TextComponent getCommandLine(T sc) {
		String line = "/" + base;
		
		// Add subcommand name
		if (sc.getKey().length() != 0) {
			line += " " + sc.getKey();
		}
		
		// Add args
		if (!sc.getArgs().getDisplay().isBlank()) {
			line += " " + sc.getArgs().getDisplay();
		}

		Builder b = Component.text().content(line);
		if (sc.getColor() != null) {
			b.color(sc.getColor());
		}
		
		// Add description
		if (sc.getDescription() != null) {
			b.append(Component.text(" - " + sc.getDescription(), NamedTextColor.GRAY));
		}
		return b.build();
	}
	
	public String getPermission() {
		return perm;
	}
	
	public String getBase() {
		return base;
	}
	
	public TextColor getColor() {
		return color;
	}
	
	public HashSet<String> getAliases() {
		return aliases;
	}
}
