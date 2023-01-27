package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.nbtapi.NBTItem;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class CmdNBTKeys extends Subcommand {
	public CmdNBTKeys(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
	}

	@Override
	public void run(CommandSender s, String[] args) {
		Player p = (Player) s;
		ItemStack item = p.getInventory().getItemInMainHand();
		NBTItem nbti = new NBTItem(item);
		for (String key : nbti.getKeys()) {
			p.sendMessage(key + " [" + nbti.getType(key) + "]");
		}
	}
}
