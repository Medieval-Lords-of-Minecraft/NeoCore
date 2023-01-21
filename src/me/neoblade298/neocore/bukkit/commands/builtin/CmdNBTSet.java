package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.nbtapi.NBTItem;
import me.neoblade298.neocore.bukkit.commands.CommandArgument;
import me.neoblade298.neocore.bukkit.commands.CommandArguments;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.commands.SubcommandRunner;

public class CmdNBTSet implements Subcommand {
	private static final CommandArguments args = new CommandArguments(new CommandArgument("key"), new CommandArgument("value (/i, /d)"));

	@Override
	public String getPermission() {
		return "neocore.nbt";
	}

	@Override
	public SubcommandRunner getRunner() {
		return SubcommandRunner.PLAYER_ONLY;
	}

	@Override
	public String getKey() {
		return "set";
	}

	@Override
	public String getDescription() {
		return "Sets nbt of item in hand";
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

	@Override
	public CommandArguments getArgs() {
		return args;
	}
}
