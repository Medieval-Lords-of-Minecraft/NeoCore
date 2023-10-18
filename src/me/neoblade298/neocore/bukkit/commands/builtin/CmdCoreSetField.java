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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

// /core setfield [player] [field]
public class CmdCoreSetField extends Subcommand {
	private static Component notOnline = Component.text("That user isn't online!", NamedTextColor.RED);
	private static Component noPerms = Component.text("You can't change this!", NamedTextColor.RED);
	public CmdCoreSetField(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("player"), new Arg("key"), new Arg("subkey"), new Arg("value"));
	}

	@Override
	public void run(CommandSender s, String[] args) {
		PlayerFields fields = PlayerDataManager.getPlayerFields(args[1]);
		Player p = Bukkit.getPlayer(args[0]);
		
		if (p == null) {
			Util.msg(s, notOnline);
			return;
		}
		
		// Must be staff to change hidden tags or tags that aren't yours
		if ((fields.isHidden() || !p.equals(s))
				&& !s.hasPermission("mycommand.staff")) {
			Util.msg(s, noPerms);
			return;
		}

		fields.changeField(args[2], args[3], p.getUniqueId());
	}
}
