package me.neoblade298.neocore.commands.builtin;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.neoblade298.neocore.commands.CommandArgument;
import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;
import me.neoblade298.neocore.player.PlayerDataManager;
import me.neoblade298.neocore.player.PlayerTags;
import me.neoblade298.neocore.util.Util;

// /core addtag [player] [tag]
public class CmdCoreRemoveTag implements Subcommand {
	private static final CommandArguments args = new CommandArguments(Arrays.asList(
			new CommandArgument("player"), new CommandArgument("key"), new CommandArgument("subkey")));

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public SubcommandRunner getRunner() {
		return SubcommandRunner.BOTH;
	}

	@Override
	public String getKey() {
		return "removetag";
	}

	@Override
	public String getDescription() {
		return "Plays a message, usable by player but hidden";
	}

	@Override
	public void run(CommandSender s, String[] args) {
		PlayerTags tags = PlayerDataManager.getPlayerTags(args[1]);
		Player p = Bukkit.getPlayer(args[0]);
		
		if (p == null) {
			Util.msg(s, "&cThat user isn't online!");
			return;
		}
		
		// Must be staff to change hidden tags or tags that aren't yours
		if ((tags.isHidden() || !p.equals(s))
				&& !s.hasPermission("mycommand.staff")) {
			Util.msg(s, "&cYou can't change this!");
			return;
		}
		
		tags.reset(args[2], p.getUniqueId());
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}
}
