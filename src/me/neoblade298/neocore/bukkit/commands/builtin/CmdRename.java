package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neocore.shared.util.SharedUtil;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;

public class CmdRename extends Subcommand {
	private static int RENAME_PRICE = 1000;
	
	public CmdRename(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.setOverride("[name]");
		args.setMin(1);
	}

	@Override
	public void run(CommandSender s, String[] args) {
		Player p = (Player) s;
		Economy econ = NeoCore.getEconomy();
		if (econ.getBalance(p) >= RENAME_PRICE) {
			ItemStack item = p.getInventory().getItemInMainHand();
			if (item != null && !item.getType().equals(Material.AIR)) {
				ItemMeta meta = item.getItemMeta();

				// Put together rename string
				String rename = SharedUtil.connectArgs(args);
				
				if (ChatColor.stripColor(rename).length() > 30 && !p.hasPermission("mycommand.staff")) {
					p.sendMessage("§4[§c§lMLMC§4] §cName must be less than 30 characters!");
					return;
				}
				
				meta.displayName(NeoCore.miniMessage().deserialize(rename));
				item.setItemMeta(meta);
				econ.withdrawPlayer(p, RENAME_PRICE);
				p.sendMessage("§4[§c§lMLMC§4] §7Successfully renamed item!");
			}
			else {
				p.sendMessage("§4[§c§lMLMC§4] §cYou're not holding anything in your mainhand!");
			}
		}
		else {
			p.sendMessage("§4[§c§lMLMC§4] §cYou don't have enough gold for this!");
		}
	}
}
