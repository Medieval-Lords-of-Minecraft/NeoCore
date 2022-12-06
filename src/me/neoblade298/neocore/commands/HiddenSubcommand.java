package me.neoblade298.neocore.commands;

import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;

public class HiddenSubcommand implements Subcommand {
	private Subcommand cmd;
	private String alias;
	public HiddenSubcommand(Subcommand cmd) {
		this.cmd = cmd;
	}
	public HiddenSubcommand(Subcommand cmd, String alias) {
		this.cmd = cmd;
		this.alias = alias;
	}

	@Override
	public String getPermission() {
		return cmd.getPermission();
	}

	@Override
	public SubcommandRunner getRunner() {
		return cmd.getRunner();
	}

	@Override
	public String getKey() {
		return alias;
	}

	@Override
	public String getDescription() {
		return cmd.getDescription();
	}

	@Override
	public void run(CommandSender s, String[] args) {
		cmd.run(s, args);
	}
	
	@Override
	public boolean isHidden() {
		return true;
	}

	@Override
	public ChatColor getColor() {
		return cmd.getColor();
	}
	@Override
	public CommandArguments getArgs() {
		return cmd.getArgs();
	}
	@Override
	public String getArgOverride() {
		return cmd.getArgOverride();
	}
}
