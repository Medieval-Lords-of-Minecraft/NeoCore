package me.neoblade298.neocore.commands.builtin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.nbtapi.NBTItem;
import me.neoblade298.neocore.commands.CommandArgument;
import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;

public class CmdFix implements Subcommand {
	private static final CommandArguments args = new CommandArguments(new CommandArgument("player", false));

	@Override
	public String getPermission() {
		return "neocore.fix";
	}

	@Override
	public SubcommandRunner getRunner() {
		return SubcommandRunner.BOTH;
	}

	@Override
	public String getKey() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Fixes given player's item in mainhand";
	}

	@Override
	public void run(CommandSender s, String[] args) {
		Player p = args.length == 1 ? Bukkit.getPlayer(args[0]) : (Player) s;
		if (fix(p, s)) {
			p.sendMessage("§4[§c§lMLMC§4] §7Successfully repaired your item!");
			Bukkit.getLogger().info("[NeoItemUtils] Successfully repaired item for " + p.getName());
		}
		else {
			p.sendMessage("§4[§c§lMLMC§4] §cFailed to repair your item! Are you sure it can be repaired?");
			Bukkit.getLogger().info("[NeoItemUtils] Failed to repair item for " + p.getName());
		}
	}
	
	private boolean fix(Player p, CommandSender s) {
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

	@Override
	public CommandArguments getArgs() {
		return args;
	}
}
