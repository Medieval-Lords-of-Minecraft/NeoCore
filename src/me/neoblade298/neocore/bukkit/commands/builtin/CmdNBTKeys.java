package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import de.tr7zw.nbtapi.NBTItem;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class CmdNBTKeys extends Subcommand {
	public CmdNBTKeys(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
	}

	@Override
	public void buildNode(LiteralArgumentBuilder<CommandSourceStack> node) {
		node.executes(ctx -> {
			Player p = (Player) ctx.getSource().getSender();
			ItemStack item = p.getInventory().getItemInMainHand();
			NBTItem nbti = new NBTItem(item);
			for (String k : nbti.getKeys()) {
				p.sendMessage(k + " [" + nbti.getType(k) + "]");
			}
			return Command.SINGLE_SUCCESS;
		});
	}
}
