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
	protected boolean hidden = false;
	protected String displayArgs = "";
	
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
	
	/** Returns the display string used in help listings.
	 *  For bukkit/Brigadier subcommands, set via setDisplayArgs().
	 *  Falls back to CommandArguments.getDisplay() (used by bungee subcommands). */
	public String getDisplayArgs() {
		return displayArgs.isEmpty() ? args.getDisplay() : displayArgs;
	}

	protected void setDisplayArgs(String d) {
		this.displayArgs = d;
	}
}
