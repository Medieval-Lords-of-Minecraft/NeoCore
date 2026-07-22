package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import de.tr7zw.nbtapi.NBTItem;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class CmdNBTSet extends Subcommand {
	public CmdNBTSet(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		setDisplayArgs("[key] [value (/i, /d)]");
	}

	@Override
	public void buildNode(LiteralArgumentBuilder<CommandSourceStack> node) {
		node.then(Commands.argument("key", StringArgumentType.word())
			.then(Commands.argument("value", StringArgumentType.word())
				.executes(ctx -> {
					Player p = (Player) ctx.getSource().getSender();
					String nbtKey = StringArgumentType.getString(ctx, "key");
					String value = StringArgumentType.getString(ctx, "value");
					ItemStack item = p.getInventory().getItemInMainHand();
					NBTItem nbti = new NBTItem(item);
					String[] parsed = value.split("\\/");
					if (parsed.length == 1) {
						nbti.setString(nbtKey, value);
						nbti.applyNBT(item);
						p.sendMessage("\u00a77Successfully set NBT as string");
					} else {
						if (parsed[1].equals("d")) {
							nbti.setDouble(nbtKey, Double.parseDouble(parsed[0]));
							nbti.applyNBT(item);
							p.sendMessage("\u00a77Successfully set NBT as double");
						} else if (parsed[1].equals("i")) {
							nbti.setInteger(nbtKey, Integer.parseInt(parsed[0]));
							nbti.applyNBT(item);
							p.sendMessage("\u00a77Successfully set NBT as int");
						}
					}
					return Command.SINGLE_SUCCESS;
				})));
	}
}
