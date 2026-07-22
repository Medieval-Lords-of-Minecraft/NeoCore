package me.neoblade298.neocore.bukkit.commands.builtin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.scheduler.SchedulerAPI;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class CmdCoreSchedule extends Subcommand {
	public CmdCoreSchedule(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
	}

	@Override
	public void buildNode(LiteralArgumentBuilder<CommandSourceStack> node) {
		node.executes(ctx -> {
			SchedulerAPI.display(ctx.getSource().getSender());
			return Command.SINGLE_SUCCESS;
		});
	}
}
