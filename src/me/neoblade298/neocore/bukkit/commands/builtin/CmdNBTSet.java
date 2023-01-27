package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.nbtapi.NBTItem;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class CmdNBTSet extends Subcommand {
	public CmdNBTSet(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("key"), new Arg("value (/i, /d)"));
	}

	@Override
	public void run(CommandSender s, String[] args) {
		Player p = (Player) s;
		ItemStack item = p.getInventory().getItemInMainHand();
		NBTItem nbti = new NBTItem(item);
		String[] parsed = args[1].split("\\/");
		if (parsed.length == 1) {
			nbti.setString(args[0], args[1]);
			nbti.applyNBT(item);
			p.sendMessage("§7Successfully set NBT as string");
		}
		else {
			if (parsed[1].equals("d")) {
				nbti.setDouble(args[0], Double.parseDouble(parsed[0]));
				nbti.applyNBT(item);
				p.sendMessage("§7Successfully set NBT as double");
			}
			else if (parsed[1].equals("i")) {
				nbti.setInteger(args[0], Integer.parseInt(parsed[0]));
				nbti.applyNBT(item);
				p.sendMessage("§7Successfully set NBT as int");
			}
		}
	}
}
