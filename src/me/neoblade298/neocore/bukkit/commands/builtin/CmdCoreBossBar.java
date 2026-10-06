package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.bar.BossBarTimer;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.util.Util;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neocore.shared.util.SharedUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CmdCoreBossBar extends Subcommand {
	private static final Component INVALID_DURATION = Component.text(
			"Duration must be a positive whole number of seconds.", NamedTextColor.RED);
	private final BossBarTimer bossBarTimer;

	public CmdCoreBossBar(String key, String desc, String perm, SubcommandRunner runner, BossBarTimer bossBarTimer) {
		super(key, desc, perm, runner);
		this.bossBarTimer = bossBarTimer;
		args.setOverride("[duration seconds] [message]");
	}

	@Override
	public void run(CommandSender sender, String[] args) {
		if (args.length < 2) {
			Util.msg(sender, Component.text("Usage: /ncore bossbar <duration seconds> <message>",
					NamedTextColor.RED));
			return;
		}

		long durationSeconds;
		try {
			durationSeconds = Long.parseLong(args[0]);
			if (durationSeconds <= 0 || durationSeconds > Long.MAX_VALUE / 20L) {
				Util.msg(sender, INVALID_DURATION);
				return;
			}
		} catch (NumberFormatException e) {
			Util.msg(sender, INVALID_DURATION);
			return;
		}

		bossBarTimer.start(durationSeconds,
				NeoCore.miniMessage().deserialize(SharedUtil.connectArgs(args, 1)));
		Util.msg(sender, Component.text("Bossbar timer started.", NamedTextColor.GREEN));
	}
}
