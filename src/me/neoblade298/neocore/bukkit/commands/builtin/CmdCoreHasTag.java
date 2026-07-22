package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.player.PlayerDataManager;
import me.neoblade298.neocore.bukkit.player.PlayerTags;
import me.neoblade298.neocore.bukkit.util.Util;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CmdCoreHasTag extends Subcommand {
	private static Component notOnline = Component.text("That user isn't online!", NamedTextColor.RED);
	private static Component noPerms = Component.text("You can't view this!", NamedTextColor.RED);

	public CmdCoreHasTag(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		setDisplayArgs("[player] [key] [subkey]");
	}

	@Override
	public void buildNode(LiteralArgumentBuilder<CommandSourceStack> node) {
		node.then(Commands.argument("player", StringArgumentType.word())
			.then(Commands.argument("key", StringArgumentType.word())
				.then(Commands.argument("subkey", StringArgumentType.word())
					.executes(ctx -> {
						CommandSender s = ctx.getSource().getSender();
						String playerName = StringArgumentType.getString(ctx, "player");
						String tagKey = StringArgumentType.getString(ctx, "key");
						String subkey = StringArgumentType.getString(ctx, "subkey");
						PlayerTags tags = PlayerDataManager.getPlayerTags(tagKey);
						Player p = Bukkit.getPlayer(playerName);
						if (p == null) { Util.msg(s, notOnline); return 0; }
						if ((tags.isHidden() || !p.equals(s)) && !s.hasPermission("mycommand.staff")) {
							Util.msg(s, noPerms); return 0;
						}
						Util.msg(s, Component.text("Tag " + tagKey + "." + subkey + " is set to " + tags.exists(subkey, p.getUniqueId()), NamedTextColor.GRAY));
						return Command.SINGLE_SUCCESS;
					}))));
	}
}
