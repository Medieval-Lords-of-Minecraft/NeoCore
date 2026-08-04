package me.neoblade298.neocore.shared.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import me.neoblade298.neocore.shared.util.PaginatedList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

@NullMarked
public class SharedCmdList<T extends @NonNull AbstractSubcommand> {
	protected String base, key;
	protected @Nullable String permission;
	protected TextColor listColor;
	protected TreeMap<@NonNull String, @NonNull T> cmds;
	protected HashSet<@NonNull String> aliases;
	protected @Nullable PaginatedList<T> pages;
	
	private static final Component OUT_OF_BOUNDS = Component.text("Page is out of bounds!").color(NamedTextColor.RED);
	
	public SharedCmdList(String key, String base, @Nullable String permission,
			TreeMap<@NonNull String, @NonNull T> cmds, HashSet<@NonNull String> aliases, TextColor listColor) {
		this.key = key;
		this.base = base;
		this.permission = permission;
		this.listColor = listColor;
		this.cmds = cmds;
		this.aliases = aliases;
	}

	@SuppressWarnings("null")
	public ArrayList<Component> run(String[] args, PermissionChecker checker) {
		PaginatedList<T> commandPages = pages;
		if (commandPages == null) {
			commandPages = new PaginatedList<T>();
			for (Entry<@NonNull String, @NonNull T> entry : cmds.entrySet()) {
				String commandKey = entry.getKey();
				T command = entry.getValue();
				if (!command.isHidden() && !aliases.contains(commandKey)) {
					commandPages.add(command);
				}
			}
			pages = commandPages;
		}
		
		if (args.length == 0 || !StringUtils.isNumeric(args[0])) {
			return getPageDisplay(commandPages, 1, checker);
		}
		else {
			return getPageDisplay(commandPages, Integer.parseInt(args[0]), checker);
		}
	}
	
	private ArrayList<Component> getPageDisplay(PaginatedList<T> commandPages, int page, PermissionChecker checker) {
		ArrayList<Component> msgs = new ArrayList<Component>();
		page = page - 1;
		if (page >= commandPages.pages() || page < 0) {
			msgs.add(OUT_OF_BOUNDS);
			return msgs;
		}

		msgs.add(Component.text("List of commands: [] = Required, {} = Optional").color(NamedTextColor.GRAY));
		for (T sc : commandPages.get(page)) {
			if (sc.isHidden()) {
				continue;
			}
			String perm = sc.getPermission() != null ? sc.getPermission() : permission;
			if (perm != null && !checker.hasPermission(perm)) {
				continue;
			}
			
			Builder b = Component.text();
			String line = "/" + base;
			TextColor color = sc.getColor() == null ? listColor : sc.getColor();
			
			// Add subcommand name
			if (sc.getKey().length() != 0) {
				line += " " + sc.getKey();
			}
			
			// Add args
			line += " " + sc.getArgs().getDisplay();
			b.append(Component.text(line, color));
			
			// Add description
			if (sc.getDescription() != null) {
				line = !line.endsWith(" ") ? " " : "";
				line += "- " + sc.getDescription();
				msgs.add(b.append(Component.text(line, NamedTextColor.GRAY)).build());
			}
		}
		
		String nextCmd = "/" + this.base + " " + (this.key.length() == 0 ? "" : this.key + " ") + (page + 2);
		String prevCmd = "/" + this.base + " " + (this.key.length() == 0 ? "" : this.key + " ") + page;
		msgs.add(commandPages.getFooter(page, nextCmd, prevCmd));
		return msgs;
	}
}
