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
import me.neoblade298.neocore.player.PlayerFields;
import me.neoblade298.neocore.util.Util;

// /core addtag [player] [tag]
public class CmdCoreResetField implements Subcommand {
	private static final CommandArguments args = new CommandArguments(Arrays.asList(
			new CommandArgument("player"), new CommandArgument("key"), new CommandArgument("subkey"),
			new CommandArgument("value")));

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
		return "setfield";
	}

	@Override
	public String getDescription() {
		return "Plays a message, usable by player but hidden";
	}

	@Override
	public void run(CommandSender s, String[] args) {
		PlayerFields fields = PlayerDataManager.getPlayerFields(args[1]);
		Player p = Bukkit.getPlayer(args[0]);
		
		if (p == null) {
			Util.msg(s, "&cThat user isn't online!");
			return;
		}
		
		// Must be staff to change hidden tags or tags that aren't yours
		if ((fields.isHidden() || !p.equals(s))
				&& !s.hasPermission("mycommand.staff")) {
			Util.msg(s, "&cYou can't change this!");
			return;
		}
		
		fields.changeField(args[2], args[3], p.getUniqueId());
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}
}
