package me.neoblade298.neocore.bungee.commands;

import java.util.HashSet;
import java.util.TreeMap;

import me.neoblade298.neocore.shared.commands.SharedCmdList;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;

public class CmdList extends Subcommand {
	private SharedCmdList<Subcommand> cmds;

	public CmdList(String key, String base, String perm, TreeMap<String, Subcommand> cmds, HashSet<String> aliases, ChatColor defaultColor) {
		super(key, "List all commands", perm, SubcommandRunner.BOTH);
		this.cmds = new SharedCmdList<Subcommand>(key, base, cmds, aliases, defaultColor);
	}
	
	public CmdList(String key, String base, String perm, TreeMap<String, Subcommand> cmds, HashSet<String> aliases, ChatColor defaultColor, ChatColor cmdColor) {
		super(key, "List all commands", perm, SubcommandRunner.BOTH);
		this.cmds = new SharedCmdList<Subcommand>(key, base, cmds, aliases, defaultColor);
		this.color = cmdColor;
	}

	@Override
	public void run(CommandSender s, String[] args) {
		for (BaseComponent[] comp : cmds.run(args)) {
			s.sendMessage(comp);
		}
	}
}
