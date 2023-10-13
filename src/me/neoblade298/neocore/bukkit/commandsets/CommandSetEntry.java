package me.neoblade298.neocore.bukkit.commandsets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;

import me.neoblade298.neocore.shared.io.Section;

public class CommandSetEntry {
	private ArrayList<String> commands;
	private int weight;
	
	public CommandSetEntry(Section cfg) {
		commands = (ArrayList<String>) cfg.getStringList("commands");
		weight = cfg.getInt("weight", 1);
	}
	
	public int getWeight() {
		return weight;
	}
	
	public void run(HashMap<String, CommandSetVariable> variables, String[] args) {
		for (String cmd : commands) {
			
			// Replace variables
			for (Entry<String, CommandSetVariable> e : variables.entrySet()) {
				int var = e.getValue().generate();
				cmd = cmd.replaceAll("%" + e.getKey() + "%", "" + var);
			}
			
			// Replace args
			int i = 1;
			while (i <= args.length) {
				cmd = cmd.replaceAll("%arg" + i + "%", args[i - 1]);
				i++;
			}
			
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		}
	}
}
