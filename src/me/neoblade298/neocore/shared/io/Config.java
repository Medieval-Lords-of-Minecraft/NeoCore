package me.neoblade298.neocore.shared.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class Config extends Section {
	private File file;
	
	public Config(Map<String, Object> map, File file) {
		super("top", map);
		this.file = file;
	}
	
	public static Config load(File file) {
		Yaml yaml = new Yaml();
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(file);
			return new Config(yaml.load(inputStream), file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save() {
		Yaml yaml = new Yaml();
		FileWriter writer;
		try {
			writer = new FileWriter(file);
			yaml.dump(map, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
