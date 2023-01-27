package me.neoblade298.neocore.bukkit.bungee;

import org.bukkit.command.CommandSender;
import me.neoblade298.neocore.shared.commands.AbstractSubcommand;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public abstract class BukkitSubcommand extends AbstractSubcommand {
	public BukkitSubcommand(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
	}

	public abstract void run(CommandSender s, String[] args);
}
