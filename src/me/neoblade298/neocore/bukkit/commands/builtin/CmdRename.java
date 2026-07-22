package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;

public class CmdRename extends Subcommand {
	private static int RENAME_PRICE = 1000;

	public CmdRename(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		setDisplayArgs("[name]");
	}

	@Override
	public void buildNode(LiteralArgumentBuilder<CommandSourceStack> node) {
		node.then(Commands.argument("name", StringArgumentType.greedyString())
			.executes(ctx -> {
				Player p = (Player) ctx.getSource().getSender();
				Economy econ = NeoCore.getEconomy();
				if (econ.getBalance(p) >= RENAME_PRICE) {
					ItemStack item = p.getInventory().getItemInMainHand();
					if (item != null && !item.getType().equals(Material.AIR)) {
						ItemMeta meta = item.getItemMeta();
						String rename = StringArgumentType.getString(ctx, "name");
						if (ChatColor.stripColor(rename).length() > 30 && !p.hasPermission("mycommand.staff")) {
							p.sendMessage("\u00a74[\u00a7c\u00a7lMLMC\u00a74] \u00a7cName must be less than 30 characters!");
							return 0;
						}
						meta.displayName(NeoCore.miniMessage().deserialize(rename));
						item.setItemMeta(meta);
						econ.withdrawPlayer(p, RENAME_PRICE);
						p.sendMessage("\u00a74[\u00a7c\u00a7lMLMC\u00a74] \u00a77Successfully renamed item!");
					} else {
						p.sendMessage("\u00a74[\u00a7c\u00a7lMLMC\u00a74] \u00a7cYou're not holding anything in your mainhand!");
					}
				} else {
					p.sendMessage("\u00a74[\u00a7c\u00a7lMLMC\u00a74] \u00a7cYou don't have enough gold for this!");
				}
				return Command.SINGLE_SUCCESS;
			}));
	}
}
