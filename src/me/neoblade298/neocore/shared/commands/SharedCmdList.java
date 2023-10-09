package me.neoblade298.neocore.shared.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import me.neoblade298.neocore.shared.util.PaginatedList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class SharedCmdList<T extends AbstractSubcommand> {
	protected String base, key, permission;
	protected ChatColor listColor;
	protected TreeMap<String, T> cmds;
	protected HashSet<String> aliases;
	protected PaginatedList<T> pages = null;
	
	private static final Component OUT_OF_BOUNDS = Component.text("Page is out of bounds!").color(NamedTextColor.RED);
	
	public SharedCmdList(String key, String base, String permission, TreeMap<String, T> cmds, HashSet<String> aliases, ChatColor listColor) {
		this.key = key;
		this.base = base;
		this.permission = permission;
		this.listColor = listColor;
		this.cmds = cmds;
		this.aliases = aliases;
	}

	public ArrayList<Component> run(String[] args, PermissionChecker checker) {
		if (pages == null) {
			pages = new PaginatedList<T>();
			for (Entry<String, T> entry : cmds.entrySet()) {
				if (!entry.getValue().isHidden() && !aliases.contains(entry.getKey())) {
					pages.add(entry.getValue());
				}
			}
		}
		
		if (args.length == 0 || !StringUtils.isNumeric(args[0])) {
			return getPageDisplay(1, checker);
		}
		else {
			return getPageDisplay(Integer.parseInt(args[0]), checker);
		}
	}
	
	private ArrayList<Component> getPageDisplay(int page, PermissionChecker checker) {
		ArrayList<Component> msgs = new ArrayList<Component>();
		page = page - 1;
		if (page >= pages.size() || page < 0) {
			msgs.add(OUT_OF_BOUNDS);
		}

		msgs.add(new ComponentBuilder("§7List of commands: [] = Required, {} = Optional").create());
		for (AbstractSubcommand sc : pages.get(page)) {
			if (sc.isHidden()) {
				continue;
			}
			String perm = sc.getPermission() != null ? sc.getPermission() : permission;
			if (perm != null && !checker.hasPermission(perm)) {
				continue;
			}
			
			String line = "";
			if (sc.getColor() == null) {
				line += listColor + "/" + base;
			}
			else {
				line += sc.getColor() + "/" + base;
			}
			
			// Add subcommand name
			if (sc.getKey().length() != 0) {
				line += " " + sc.getKey();
			}
			
			// Add args
			line += " " + sc.getArgs().getDisplay();
			
			// Add description
			if (sc.getDescription() != null) {
				if (!line.endsWith(" ")) line += " ";
				line += "§7- " + sc.getDescription();
				msgs.add(new ComponentBuilder(line).create());
			}
		}
		
		String nextCmd = "/" + this.base + " " + (this.key.length() == 0 ? "" : this.key + " ") + (page + 2);
		String prevCmd = "/" + this.base + " " + (this.key.length() == 0 ? "" : this.key + " ") + page;
		msgs.add(pages.getFooter(page, nextCmd, prevCmd));
		return msgs;
	}
}
