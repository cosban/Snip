package net.cosban.snip.files;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.cosban.snip.managers.FileManager;

import com.nikhaldimann.inieditor.IniEditor;

public abstract class SnipFile {
	protected FileManager	files;
	protected File			file;
	protected IniEditor		ini;
	public String			_fileName;

	private final String	prefix	= "./plugins/snip/";

	public SnipFile(FileManager instance, String fileName) throws IOException {
		files = instance;
		_fileName = fileName;
		ini = new IniEditor(true);
		file = new File(prefix, _fileName + ".ini");
		file.getParentFile().mkdirs();
		file.createNewFile();
		ini.load(file);
	}

	public String getFileName() {
		return _fileName;
	}

	public File getFile() {
		return file;
	}

	public void save() throws IOException {
		ini.save(file);
	}

	public List<String> getSets() {
		return ini.sectionNames();
	}
}
