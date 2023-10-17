package me.neoblade298.neocore.shared.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class Config extends Section {
	private File file;
	
	public Config(Map<Object, Object> temp, File file) {
		super(null, temp); // Configs never have a key, only sections do
		this.parent = this;
		this.file = file;
	}
	
	public static Config load(File file) {
		Yaml yaml = new Yaml();
		try {
			InputStream inputStream = new FileInputStream(file);
			Map<Object, Object> map = yaml.load(inputStream);
			if (map == null) {
				map = new HashMap<Object, Object>();
			}
			return new Config(map, file);
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
	
	public File getFile() {
		return file;
	}
}
