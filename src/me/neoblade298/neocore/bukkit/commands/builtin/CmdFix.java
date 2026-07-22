package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class CmdFix extends Subcommand {
	public CmdFix(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		setDisplayArgs("{player}");
	}

	@Override
	public void buildNode(LiteralArgumentBuilder<CommandSourceStack> node) {
		// /fix  - fix self
		node.executes(ctx -> {
				Player p = (Player) ctx.getSource().getSender();
				if (fix(p, p)) {
					p.sendMessage("\u00a74[\u00a7c\u00a7lMLMC\u00a74] \u00a77Successfully repaired your item!");
					Bukkit.getLogger().info("[NeoItemUtils] Successfully repaired item for " + p.getName());
				} else {
					p.sendMessage("\u00a74[\u00a7c\u00a7lMLMC\u00a74] \u00a7cFailed to repair your item! Are you sure it can be repaired?");
					Bukkit.getLogger().info("[NeoItemUtils] Failed to repair item for " + p.getName());
				}
				return Command.SINGLE_SUCCESS;
			})
			// /fix <player>  - fix named player
			.then(Commands.argument("player", StringArgumentType.word())
				.executes(ctx -> {
					CommandSender s = ctx.getSource().getSender();
					Player p = Bukkit.getPlayer(StringArgumentType.getString(ctx, "player"));
					if (p == null) { s.sendMessage("\u00a7cThat player is not online!"); return 0; }
					if (fix(p, s)) {
						p.sendMessage("\u00a74[\u00a7c\u00a7lMLMC\u00a74] \u00a77Successfully repaired your item!");
						Bukkit.getLogger().info("[NeoItemUtils] Successfully repaired item for " + p.getName());
					} else {
						p.sendMessage("\u00a74[\u00a7c\u00a7lMLMC\u00a74] \u00a7cFailed to repair your item! Are you sure it can be repaired?");
						Bukkit.getLogger().info("[NeoItemUtils] Failed to repair item for " + p.getName());
					}
					return Command.SINGLE_SUCCESS;
				}));
	}

	private static boolean fix(Player p, CommandSender s) {
		ItemStack item = p.getInventory().getItemInMainHand();
		if (!item.hasItemMeta()) return false;
		ItemMeta meta = item.getItemMeta();
		if (meta.isUnbreakable()) return false;
		if (item.getType().getMaxDurability() == 0) return false;
		if ((!meta.isUnbreakable()) && (item.getType().getMaxDurability() > 0)) {
			((Damageable) meta).setDamage(0);
		}
		item.setItemMeta(meta);
		return true;
	}
}
