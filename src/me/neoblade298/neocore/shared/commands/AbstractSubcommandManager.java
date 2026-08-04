package me.neoblade298.neocore.shared.commands;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

@NullMarked
public abstract class AbstractSubcommandManager<T extends @NonNull AbstractSubcommand> {
	protected TreeMap<@NonNull String, @NonNull T> handlers = new TreeMap<>();
	protected HashSet<@NonNull String> aliases = new HashSet<>();
	protected String base;
	protected @Nullable String perm;
	protected TextColor color = NamedTextColor.RED;
	
	public AbstractSubcommandManager(String base, @Nullable String perm, TextColor color) {
		this.base = base;
		this.perm = perm;
		this.color = color;
	}
	
	public @Nullable T parseForCommand(String[] args) {
		// Run command no args
		if (args.length == 0) {
			if (handlers.containsKey("") ) {
				return lookup("");
			}
			else {
				return null;
			}
		}
		// Run no-arg command with a number value, specifically for command lists
		else if (StringUtils.isNumeric(args[0])) {
			@Nullable T baseCommand = lookup("");
			if (baseCommand != null && baseCommand.acceptsNumericFirstArg()) return baseCommand;
		}
		// Run command normally
		else if (handlers.containsKey(args[0].toLowerCase())) {
			return lookup(args[0].toLowerCase());
		}
		// Run base command with args (like /rename [variable])
		else if (!handlers.containsKey(args[0].toLowerCase()) && handlers.containsKey("")) {
			@Nullable T cmd = lookup("");
			if (cmd == null) return null;
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
	
	public @Nullable T getCommand(String key) {
		return lookup(key.toLowerCase());
	}

	protected @Nullable T lookup(String key) {
		return handlers.get(key);
	}
	
	public Set<@NonNull String> getKeys() {
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
	
	public @Nullable String getPermission() {
		return perm;
	}
	
	public String getBase() {
		return base;
	}
	
	public TextColor getColor() {
		return color;
	}
	
	public HashSet<@NonNull String> getAliases() {
		return aliases;
	}
}
