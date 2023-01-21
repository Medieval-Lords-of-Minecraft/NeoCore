package me.neoblade298.neocore.bukkit.commands;

import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;

public interface Subcommand {
	public String getPermission();
	public SubcommandRunner getRunner();
	public String getKey();
	public default String[] getAliases() { return null; }
	public String getDescription();
	public default ChatColor getColor() { return null; }
	public default CommandArguments getArgs() { return null; }
	public default String getArgOverride() { return null; }
	public default boolean isHidden() { return false; }
	
	public void run(CommandSender s, String[] args);
}
