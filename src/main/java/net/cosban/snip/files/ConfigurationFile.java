package net.cosban.snip.files;

import java.io.IOException;

import net.cosban.snip.Snip;
import net.cosban.snip.managers.FileManager;

public class ConfigurationFile extends SnipFile {

	// TODO: load into memory for reloads
	public ConfigurationFile(FileManager files, String fileName) throws IOException {
		super(files, fileName);
		if (!ini.hasSection("config")) {
			ini.addSection("config");
			ini.addComment("config", "This is the configuration file, read the instructions located at cosban.net for more information\nYou were running Snip v"
					+ Snip.getVersion()
					+ " when this file was generated");
			ini.set("config", "server-id", "0");
			ini.set("config", "debug", "false");

		}
		if (!ini.hasSection("mysql")) {
			ini.set("mysql", "hostname", "localhost");
			ini.set("mysql", "port", "3306");
			ini.set("mysql", "username", "root");
			ini.set("mysql", "database", "minecraft");
			ini.set("mysql", "table-prefix", "snip");
			ini.set("mysql", "password", "yesHorseBatteryStaple!");
		}
		files.save(this);
	}

	public int getServerID() {
		return Integer.valueOf(ini.get("config", "server-id"));
	}

	public void setServerID(int server_id) {
		ini.set("config", "server-id", String.valueOf(server_id));
		files.save(this);
	}

	public boolean toUseDebug() {
		return Boolean.valueOf(ini.get("config", "debug"));
	}

	public void setToUseDebug(boolean debug) {
		ini.set("config", "debug", String.valueOf(debug));
		files.save(this);
	}

	public String getHostname() {
		return ini.get("mysql", "hostname");
	}

	public void setHostName(String hostname) {
		ini.set("mysql", "hostname", hostname);
		files.save(this);
	}

	public String getURL() {
		return "jdbc:mysql://" + getHostname() + getPort() + getDatabase() + "?useUnicode=true&characterEncoding=utf-8";
	}

	public int getPort() {
		return Integer.valueOf(ini.get("mysql", "port"));
	}

	public void setPort(int i) {
		ini.set("mysql", "port", String.valueOf(i));
		files.save(this);
	}

	public String getUsername() {
		return ini.get("mysql", "username");
	}

	public void setUsername(String s) {
		ini.set("mysql", "username", s);
	}

	public String getDatabase() {
		return ini.get("mysql", "database");
	}

	public void setDatabase(String s) {
		ini.set("mysql", "database", s);
	}

	public String getPrefix() {
		return ini.get("mysql", "table-prefix");
	}

	public void setPrefix(String s) {
		ini.set("mysql", "table-prefix", s);
	}

	public String getPassword() {
		return ini.get("mysql", "password");
	}

	public void setPassword(String password) {
		ini.set("mysql", "password", password);
		files.save(this);
	}

}
