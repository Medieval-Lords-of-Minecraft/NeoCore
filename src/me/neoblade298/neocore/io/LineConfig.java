package me.neoblade298.neocore.io;

import java.util.HashMap;

public class LineConfig {
	private String key, line, fullLine;
	private HashMap<String, String> args = new HashMap<String, String>();
	public LineConfig(String line) {
		fullLine = line;
		int keyIndex = line.indexOf(' ') == -1 ? line.length() : line.indexOf(' ');
		int lineIndex = line.indexOf('>');
		
		String argLine;
		key = line.substring(0, keyIndex == -1 ? line.length() : keyIndex).toLowerCase();
		if (key.indexOf(':') != -1) {
			key = "";
			keyIndex = 0; // If there's a :, there's no key, use keyless system
		}
		
		
		if (lineIndex != -1) {
			this.line = line.substring(lineIndex + 1).trim().replaceAll("&", "§");
			argLine = line.substring(keyIndex, lineIndex).trim();
		}
		else {
			this.line = "";
			argLine = line.substring(keyIndex).trim();
		}
		String[] argEntries = argLine.split(" ");
		for (String entry : argEntries) {
			String[] arg = entry.split(":");
			if (arg.length == 2) {
				args.put(arg[0], arg[1]);
			}
		}
	}
	
	public String getKey() {
		return key;
	}
	
	public String getLine() {
		return line;
	}
	
	public String getFullLine() {
		return fullLine;
	}
	
	public String getString(String[] keys, String def) {
		for (String key : keys) {
			if (args.containsKey(key)) {
				return args.get(key);
			}
		}
		return def;
	}
	
	public String getString(String key, String def) {
		return args.getOrDefault(key, def);
	}
	
	public boolean getBool(String[] keys, boolean def) {
		for (String key : keys) {
			if (args.containsKey(key)) {
				return args.get(key).equalsIgnoreCase("true");
			}
		}
		return def;
	}
	
	public boolean getBool(String key, boolean def) {
		if (args.containsKey(key)) {
			return args.get(key).equalsIgnoreCase("true");
		}
		return def;
	}
	
	public int getInt(String[] keys, int def) {
		for (String key : keys) {
			if (args.containsKey(key)) {
				return Integer.parseInt(args.get(key));
			}
		}
		return def;
	}
	
	public int getInt(String key, int def) {
		if (args.containsKey(key)) {
			return Integer.parseInt(args.get(key));
		}
		return def;
	}
	
	public double getDouble(String[] keys, double def) {
		for (String key : keys) {
			if (args.containsKey(key)) {
				return Double.parseDouble(args.get(key));
			}
		}
		return def;
	}
	
	public double getDouble(String key, double def) {
		if (args.containsKey(key)) {
			return Double.parseDouble(args.get(key));
		}
		return def;
	}
}
