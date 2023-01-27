package me.neoblade298.neocore.shared.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import me.neoblade298.neocore.shared.util.PaginatedList;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class SharedCmdList<T extends AbstractSubcommand> {
	protected String base, key;
	protected ChatColor listColor;
	protected TreeMap<String, T> cmds;
	protected HashSet<String> aliases;
	protected PaginatedList<T> pages = null;
	
	public SharedCmdList(String key, String base, TreeMap<String, T> cmds, HashSet<String> aliases, ChatColor listColor) {
		this.key = key;
		this.base = base;
		this.listColor = listColor;
		this.cmds = cmds;
		this.aliases = aliases;
	}

	public ArrayList<BaseComponent[]> run(String[] args) {
		if (pages == null) {
			pages = new PaginatedList<T>();
			for (T cmd : cmds.values()) {
				if (!cmd.isHidden() && !aliases.contains(cmd.getKey())) {
					pages.add(cmd);
				}
			}
		}
		
		if (args.length == 0 || StringUtils.isNumeric(args[0])) {
			return getPageDisplay(1);
		}
		else {
			return getPageDisplay(Integer.parseInt(args[0]));
		}
	}
	
	private ArrayList<BaseComponent[]> getPageDisplay(int page) {
		ArrayList<BaseComponent[]> msgs = new ArrayList<BaseComponent[]>();
		page = page - 1;
		if (page >= pages.size() || page < 0) {
			msgs.add(new ComponentBuilder("&cPage is out of bounds!").create());
		}

		msgs.add(new ComponentBuilder("§7List of commands: [] = Required, {} = Optional").create());
		for (AbstractSubcommand sc : pages.get(page)) {
			if (sc.isHidden()) {
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
