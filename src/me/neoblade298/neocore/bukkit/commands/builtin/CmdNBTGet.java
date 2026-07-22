package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class CmdNBTGet extends Subcommand {
	public CmdNBTGet(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		setDisplayArgs("[key]");
	}

	@Override
	public void buildNode(LiteralArgumentBuilder<CommandSourceStack> node) {
		node.then(Commands.argument("key", StringArgumentType.word())
			.executes(ctx -> {
				Player p = (Player) ctx.getSource().getSender();
				String nbtKey = StringArgumentType.getString(ctx, "key");
				ItemStack item = p.getInventory().getItemInMainHand();
				NBTItem nbti = new NBTItem(item);
				NBTType type = nbti.getType(nbtKey);
				if (type == NBTType.NBTTagInt) {
					p.sendMessage("Type: Integer");
					p.sendMessage("" + nbti.getInteger(nbtKey));
				} else if (type == NBTType.NBTTagDouble) {
					p.sendMessage("Type: Double");
					p.sendMessage("" + nbti.getDouble(nbtKey));
				} else if (type == NBTType.NBTTagString) {
					p.sendMessage("Type: String");
					p.sendMessage(nbti.getString(nbtKey));
				}
				return Command.SINGLE_SUCCESS;
			}));
	}
}
