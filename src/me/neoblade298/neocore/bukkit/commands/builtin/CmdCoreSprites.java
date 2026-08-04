package me.neoblade298.neocore.bukkit.commands.builtin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.util.Util;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;

public class CmdCoreSprites extends Subcommand {
	private static final int SPRITES_PER_ROW = 8;
	private static final int SPRITES_PER_PAGE = 48;

	public CmdCoreSprites(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
	}

	@Override
	public void run(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		List<Material> materials = Arrays.stream(Material.values())
				.filter(material -> !material.isLegacy() && material.isBlock())
				.filter(material -> !Util.materialToSprite(material).equals(Component.empty()))
				.sorted((first, second) -> first.getKey().value().compareTo(second.getKey().value()))
				.toList();

		int pageCount = Math.max(1, (materials.size() + SPRITES_PER_PAGE - 1) / SPRITES_PER_PAGE);
		int page = parsePage(args, pageCount);
		if (page == -1) {
			Util.msg(player, Component.text("Page must be between 1 and " + pageCount + ".", NamedTextColor.RED));
			return;
		}

		player.sendMessage(Component.text("Block sprites " + page + "/" + pageCount, NamedTextColor.GOLD));
		int start = (page - 1) * SPRITES_PER_PAGE;
		int end = Math.min(start + SPRITES_PER_PAGE, materials.size());
		for (int rowStart = start; rowStart < end; rowStart += SPRITES_PER_ROW) {
			Component row = Component.empty();
			for (int index = rowStart; index < Math.min(rowStart + SPRITES_PER_ROW, end); index++) {
				Material material = materials.get(index);
				row = row.append(Util.materialToSprite(material)
						.hoverEvent(Component.text(material.getKey().asString(), NamedTextColor.GRAY)))
						.appendSpace();
			}
			player.sendMessage(row);
		}

		player.sendMessage(navigation(page, pageCount));
	}

	private static int parsePage(String[] args, int pageCount) {
		if (args.length == 0) return 1;
		try {
			int page = Integer.parseInt(args[0]);
			return page >= 1 && page <= pageCount ? page : -1;
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	private static Component navigation(int page, int pageCount) {
		Component navigation = Component.empty();
		if (page > 1) {
			navigation = navigation.append(pageLink("< Previous", page - 1));
		}
		if (page > 1 && page < pageCount) {
			navigation = navigation.append(Component.text(" | ", NamedTextColor.DARK_GRAY));
		}
		if (page < pageCount) {
			navigation = navigation.append(pageLink("Next >", page + 1));
		}
		return navigation;
	}

	private static Component pageLink(String label, int page) {
		return Component.text(label, NamedTextColor.AQUA)
				.clickEvent(ClickEvent.runCommand("/ncore sprites " + page));
	}
}