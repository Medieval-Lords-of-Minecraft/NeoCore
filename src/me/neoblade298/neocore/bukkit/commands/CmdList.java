package me.neoblade298.neocore.bukkit.commands;

import java.util.HashSet;
import java.util.TreeMap;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SharedCmdList;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;

public class CmdList extends Subcommand {
	private SharedCmdList<Subcommand> cmds;
	
	public CmdList(String key, String base, String perm, String basePerm, TreeMap<String, Subcommand> cmds, HashSet<String> aliases, ChatColor defaultColor) {
		super(key, "List all commands", perm, SubcommandRunner.BOTH);
		this.cmds = new SharedCmdList<Subcommand>(key, base, basePerm, cmds, aliases, defaultColor);
		args.add(new Arg("page", false));
	}
	
	public CmdList(String key, String base, String perm, String basePerm, TreeMap<String, Subcommand> cmds, HashSet<String> aliases, ChatColor defaultColor, ChatColor cmdColor) {
		super(key, "List all commands", perm, SubcommandRunner.BOTH);
		this.cmds = new SharedCmdList<Subcommand>(key, base, basePerm, cmds, aliases, defaultColor);
		this.color = cmdColor;
		args.add(new Arg("page", false));
	}

	@Override
	public void run(CommandSender s, String[] args) {
		for (Component comp : cmds.run(args, (perm) -> { return s.hasPermission(perm); })) {
			s.sendMessage(comp);
		}
	}
}
