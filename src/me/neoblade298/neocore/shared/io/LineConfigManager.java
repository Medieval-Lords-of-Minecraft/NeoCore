package me.neoblade298.neocore.shared.io;

import java.util.HashMap;

import org.bukkit.plugin.java.JavaPlugin;

import me.neoblade298.neocore.shared.exceptions.NeoIOException;

public class LineConfigManager<A> {
	private HashMap<String, LineConfigParser<A>> parsers = new HashMap<String, LineConfigParser<A>>();
	private String name;
	
	public LineConfigManager(JavaPlugin plugin, String name) {
		this.name = plugin.getName() + "-" + name;
	}
	
	public void register(LineConfigParser<A> parser) {
		parsers.put(parser.getKey(), parser);
	}
	
	public A get(LineConfig cfg) throws NeoIOException {
		if (parsers.containsKey(cfg.getKey())) {
			return (A) parsers.get(cfg.getKey()).create(cfg);
		}
		else {
			throw new NeoIOException(name + " Config Manager Exception: Could not find key " + cfg.getKey());
		}
	}
}
