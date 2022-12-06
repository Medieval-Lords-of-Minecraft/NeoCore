package me.neoblade298.neocore.commandsets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.configuration.ConfigurationSection;

import me.neoblade298.neocore.NeoCore;

public class CommandSet {
	private String key;
	private ArrayList<CommandSetEntry> entries = new ArrayList<CommandSetEntry>();
	private HashMap<String, CommandSetVariable> variables = new HashMap<String, CommandSetVariable>();
	private int totalWeight = 0;
	
	public CommandSet(String key, ConfigurationSection cfg) {
		this.key = key;
		
		if (cfg.contains("variables")) {
			ConfigurationSection sec = cfg.getConfigurationSection("variables");
			for (String var : sec.getKeys(false)) {
				variables.put(var, new CommandSetVariable(sec.getConfigurationSection(var)));
			}
		}
		
		ConfigurationSection sec = cfg.getConfigurationSection("entries");
		for (String entry : sec.getKeys(false)) {
			CommandSetEntry cse = new CommandSetEntry(sec.getConfigurationSection(entry));
			entries.add(cse);
			totalWeight += cse.getWeight();
		}
	}
	
	public void run(String args[]) {
		int gen = NeoCore.gen.nextInt(totalWeight);
		Iterator<CommandSetEntry> iter = entries.iterator();
		CommandSetEntry e = null;
		while (gen >= 0 && iter.hasNext()) {
			e = iter.next();
			gen -= e.getWeight();
		}
		e.run(variables, args);
	}
	
	public String getKey() {
		return key;
	}
}
