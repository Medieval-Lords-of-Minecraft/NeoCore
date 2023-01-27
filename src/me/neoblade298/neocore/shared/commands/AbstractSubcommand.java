package me.neoblade298.neocore.shared.commands;

import net.md_5.bungee.api.ChatColor;

public abstract class AbstractSubcommand {
	protected String key;
	protected String desc;
	protected String perm;
	protected SubcommandRunner runner;
	protected String[] aliases;
	protected ChatColor color;
	protected CommandArguments args = new CommandArguments();
	protected boolean hidden = false;
	
	public AbstractSubcommand(String key, String desc, String perm, SubcommandRunner runner) {
		this.key = key;
		this.desc = desc;
		this.perm = perm;
		this.runner = runner;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getDescription() {
		return desc;
	}
	
	public SubcommandRunner getRunner() {
		return runner;
	}
	
	public String getPermission() {
		return perm;
	}
	
	public String[] getAliases() {
		return aliases;
	}
	
	public ChatColor getColor() {
		return color;
	}
	
	public CommandArguments getArgs() {
		return args;
	}
	
	public boolean isHidden() {
		return hidden;
	}
	
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
}
