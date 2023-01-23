package me.neoblade298.neocore.bukkit.commands.builtin;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.neoblade298.neocore.bukkit.commands.CommandArgument;
import me.neoblade298.neocore.bukkit.commands.CommandArguments;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.commands.SubcommandRunner;
import me.neoblade298.neocore.bukkit.player.PlayerDataManager;
import me.neoblade298.neocore.bukkit.player.PlayerFields;
import me.neoblade298.neocore.shared.util.SharedUtil;

// /core addtag [player] [tag]
public class CmdCoreSetField implements Subcommand {
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
			SharedUtil.msg(s, "&cThat user isn't online!");
			return;
		}
		
		// Must be staff to change hidden tags or tags that aren't yours
		if ((fields.isHidden() || !p.equals(s))
				&& !s.hasPermission("mycommand.staff")) {
			SharedUtil.msg(s, "&cYou can't change this!");
			return;
		}
		
		fields.resetField(args[2], p.getUniqueId());
	}

	@Override
	public CommandArguments getArgs() {
		return args;
	}
}