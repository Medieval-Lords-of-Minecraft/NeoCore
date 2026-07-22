package me.neoblade298.neocore.bukkit.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.neoblade298.neocore.shared.commands.AbstractSubcommandManager;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import net.kyori.adventure.text.format.TextColor;

public class SubcommandManager extends AbstractSubcommandManager<Subcommand> {

	public SubcommandManager(String base, String perm, TextColor color) {
		super(base, perm, color);
	}

	public void registerCommandList() {
		registerCommandList("", null, null);
	}

	public void registerCommandList(String key) {
		registerCommandList(key, null, null);
	}

	public void registerCommandList(String key, String perm, TextColor color) {
		handlers.put(key.toLowerCase(), new CmdList(key, base, perm, super.perm, handlers, aliases, this.color, color));
	}

	/**
	 * Builds the Brigadier command tree from all registered subcommands and
	 * registers it with the Paper command system.
	 */
	public void registerTo(Commands commands) {
		LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(base);

		for (Map.Entry<String, Subcommand> entry : handlers.entrySet()) {
			String key = entry.getKey();
			Subcommand sc = entry.getValue();

			// Skip alias map entries - they're handled below when processing the primary key
			if (aliases.contains(key)) continue;

			if (key.isEmpty()) {
				// Base command (e.g. CmdList or a root-action command like /fix).
				// For real action commands (not CmdList), apply requires to the root literal.
				if (!(sc instanceof CmdList)) {
					root.requires(buildRequirement(sc));
				}
				sc.buildNode(root);
			} else {
				// Regular subcommand literal
				LiteralArgumentBuilder<CommandSourceStack> literal = buildLiteral(key, sc);
				sc.buildNode(literal);
				root.then(literal);

				// Subcommand aliases become sibling literals in the tree
				if (sc.getAliases() != null) {
					for (String alias : sc.getAliases()) {
						LiteralArgumentBuilder<CommandSourceStack> aliasLiteral = buildLiteral(alias, sc);
						sc.buildNode(aliasLiteral);
						root.then(aliasLiteral);
					}
				}
			}
		}

		commands.register(root.build(), base + " command", List.of());
	}

	private LiteralArgumentBuilder<CommandSourceStack> buildLiteral(String key, Subcommand sc) {
		return Commands.literal(key).requires(buildRequirement(sc));
	}

	private Predicate<CommandSourceStack> buildRequirement(Subcommand sc) {
		String effectivePerm = sc.getPermission() != null ? sc.getPermission() : perm;
		List<Predicate<CommandSourceStack>> conditions = new ArrayList<>();

		if (effectivePerm != null) {
			final String fp = effectivePerm;
			conditions.add(src -> src.getSender().hasPermission(fp));
		}
		if (sc.getRunner() == SubcommandRunner.PLAYER_ONLY) {
			conditions.add(src -> src.getSender() instanceof Player);
		} else if (sc.getRunner() == SubcommandRunner.CONSOLE_ONLY) {
			conditions.add(src -> src.getSender() instanceof ConsoleCommandSender);
		}

		if (conditions.isEmpty()) return src -> true;
		return conditions.stream().reduce(src -> true, Predicate::and);
	}
}
