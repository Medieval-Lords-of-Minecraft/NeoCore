package me.neoblade298.neocore.shared.commands;

import net.kyori.adventure.text.format.TextColor;

public abstract class AbstractSubcommand {
	protected String key;
	protected String desc;
	protected String perm;
	protected SubcommandRunner runner;
	protected String[] aliases;
	protected TextColor color;
	protected CommandArguments args = new CommandArguments();
	protected boolean hidden = false, tabEnabled = false;
	
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
	
	public TextColor getColor() {
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
	
	public boolean isTabEnabled() {
		return tabEnabled;
	}
	
	public void setTabEnabled(boolean enabled) {
		this.tabEnabled = enabled;
	}
	
	public AbstractSubcommand enableTabComplete() {
		this.tabEnabled = true;
		return this;
	}
}
