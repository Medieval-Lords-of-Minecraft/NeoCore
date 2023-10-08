package me.neoblade298.neocore.shared.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.Yaml;

public class Config {
	private String key;
	private Map<String, Object> map;
	
	public Config(String key, Map<String, Object> map) {
		this.key = key;
		this.map = map;
	}
	
	public static Config load(File file) {
		Yaml yaml = new Yaml();
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(file);
			return new Config(null, yaml.load(inputStream));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Set<String> getKeys() {
		return map.keySet();
	}
	
	public int getInt(String key) {
		return (int) map.get(key);
	}
	
	public int getInt(String key, int def) {
		return (int) map.get(key);
	}
	
	public double getDouble(String key) {
		return (double) map.get(key);
	}
	
	public double getDouble(String key, double def) {
		return (double) map.get(key);
	}
	
	public String getString(String key, String def) {
		return (String) map.get(key);
	}
	
	public String getString(String key) {
		return (String) map.get(key);
	}
	
	public boolean contains(String key) {
		return map.containsKey(key);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getStringList(String key) {
		return (List<String>) map.get(key);
	}
	
	@SuppressWarnings("unchecked")
	public Config getSection(String key) {
		return new Config(key, (Map<String, Object>) map.get(key));
	}
	
	public String getName() {
		return key;
	}
}
