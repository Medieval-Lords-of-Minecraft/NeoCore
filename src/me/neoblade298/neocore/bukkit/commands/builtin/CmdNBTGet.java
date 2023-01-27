package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTType;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class CmdNBTGet extends Subcommand {
	public CmdNBTGet(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("key"));
	}

	@Override
	public void run(CommandSender s, String[] args) {
		Player p = (Player) s;
		ItemStack item = p.getInventory().getItemInMainHand();
		NBTItem nbti = new NBTItem(item);
		NBTType type = nbti.getType(args[0]);
		if (type == NBTType.NBTTagInt) {
			p.sendMessage("Type: Integer");
			p.sendMessage("" + nbti.getInteger(args[0]));
		}
		if (type == NBTType.NBTTagDouble) {
			p.sendMessage("Type: Double");
			p.sendMessage("" + nbti.getDouble(args[0]));
		}
		if (type == NBTType.NBTTagString) {
			p.sendMessage("Type: String");
			p.sendMessage(nbti.getString(args[0]));
		}
	}
}
