package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTType;
import me.neoblade298.neocore.bukkit.commands.CommandArgument;
import me.neoblade298.neocore.bukkit.commands.CommandArguments;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.commands.SubcommandRunner;

public class CmdNBTGet implements Subcommand {
	private static final CommandArguments args = new CommandArguments(new CommandArgument("key"));

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
		return "get";
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

	@Override
	public CommandArguments getArgs() {
		return args;
	}
}
