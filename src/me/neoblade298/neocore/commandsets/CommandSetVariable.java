package me.neoblade298.neocore.commandsets;

import org.bukkit.configuration.ConfigurationSection;

import me.neoblade298.neocore.NeoCore;

public class CommandSetVariable {
	private int min, max, multiplier;

	public CommandSetVariable(ConfigurationSection cfg) {
		this.min = cfg.getInt("min", 1);
		this.max = cfg.getInt("max", 1);
		this.multiplier = cfg.getInt("multiplier", 1);
	}
	
	public int generate() {
		int rand = NeoCore.gen.nextInt(max - min + 1) + min; // Min and max inclusive
		return rand * multiplier;
	}
}
