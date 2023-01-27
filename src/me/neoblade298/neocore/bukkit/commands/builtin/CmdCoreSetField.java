package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.player.PlayerDataManager;
import me.neoblade298.neocore.bukkit.player.PlayerFields;
import me.neoblade298.neocore.bukkit.util.Util;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

// /core addtag [player] [tag]
public class CmdCoreSetField extends Subcommand {
	public CmdCoreSetField(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("player"), new Arg("key"), new Arg("subkey"));
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
		
		fields.resetField(args[2], p.getUniqueId());
	}
}
