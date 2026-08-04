package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.util.Util;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neocore.shared.util.SharedUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CmdCoreBroadcast extends Subcommand {
	private static Component error = Component.text("You need a message to broadcast!", NamedTextColor.RED);
	public CmdCoreBroadcast(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.setOverride("[broadcast]");
	}

	@Override
	public void run(CommandSender s, String[] args) {
		if (args.length == 0) {
			Util.msg(s, error);
		}
		else {
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.sendMessage(NeoCore.miniMessage().deserialize(SharedUtil.connectArgs(args)));
			}
		}
	}
}
