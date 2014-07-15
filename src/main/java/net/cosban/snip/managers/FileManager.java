package net.cosban.snip.managers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.cosban.snip.Snip;
import net.cosban.snip.files.ConfigurationFile;
import net.cosban.snip.files.SnipFile;

public class FileManager {
	private Map<String, SnipFile>	fileMap	= new HashMap<>();
	public static String			prefix	= "plugins/suckchat/";

	public FileManager(Snip instance) {
		try {
			fileMap.put("configuration", new ConfigurationFile(this, "configuration"));
			for (SnipFile f : fileMap.values()) {
				save(f);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public SnipFile getFile(String fileName) {
		if (fileMap.containsKey(fileName)) return (SnipFile) fileMap.get(fileName);
		return null;
	}

	public void save(SnipFile f) {
		try {
			f.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
