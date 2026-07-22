package me.neoblade298.neocore.bungee.commands;

import com.velocitypowered.api.command.CommandSource;

import me.neoblade298.neocore.shared.commands.AbstractSubcommand;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public abstract class Subcommand extends AbstractSubcommand {
	private boolean tabEnabled = true;

	public Subcommand(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
	}

	public abstract void run(CommandSource s, String[] args);

	public boolean isTabEnabled() {
		return tabEnabled;
	}

	public void overrideTabHandler() {
		this.tabEnabled = false;
	}
}
