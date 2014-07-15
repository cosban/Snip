package net.cosban.snip;

import java.sql.Connection;
import java.util.logging.Logger;

import net.cosban.snip.commands.BanCommand;
import net.cosban.snip.commands.Ban_IPCommand;
import net.cosban.snip.commands.KickCommand;
import net.cosban.snip.commands.ListBansCommand;
import net.cosban.snip.commands.LookupCommand;
import net.cosban.snip.commands.SnipCommand;
import net.cosban.snip.commands.TempBanCommand;
import net.cosban.snip.commands.UnbanCommand;
import net.cosban.snip.commands.Unban_IPCommand;
import net.cosban.snip.events.FEventHandler;
import net.cosban.snip.events.PlayerConnectionHandler;
import net.cosban.snip.files.ConfigurationFile;
import net.cosban.snip.managers.FileManager;
import net.cosban.snip.sql.SQLReader;
import net.cosban.snip.sql.SQLWriter;
import net.cosban.utils.Debugger;
import net.cosban.utils.SQLConnectionPool;
import net.md_5.bungee.api.plugin.Plugin;

public class Snip extends Plugin {
	private static String			version;
	private static Debugger			debug;
	private static FileManager		files;
	private static SQLReader	reader;
	private static SQLWriter	writer;
	private boolean					connected;
	private SQLConnectionPool		pool;

	protected static Logger			logger;

	public void onEnable() {
		version = getDescription().getVersion();
		files = new FileManager(this);
		debug = new Debugger(getClass().getName(), getConfig().toUseDebug());
		logger = getLogger();

		getProxy().getPluginManager().registerCommand(this, new BanCommand("ban", "snip.ban", new String[] {}));
		getProxy().getPluginManager().registerCommand(this, new UnbanCommand("unban", "snip.unban", new String[] {}));
		getProxy().getPluginManager().registerCommand(this, new Ban_IPCommand("ban-ip", "snip.banip",
				new String[] { "banip", "ipban", "ip-ban" }));
		getProxy().getPluginManager().registerCommand(this, new Unban_IPCommand("unban-ip", "snip.unbanip",
				new String[] { "unbanip", "ipunban", "ip-unban" }));
		getProxy().getPluginManager().registerCommand(this, new LookupCommand("lookup", "snip.lookup", new String[] {}));
		getProxy().getPluginManager().registerCommand(this, new ListBansCommand("listbans", "snip.listbans",
				new String[] {}));
		getProxy().getPluginManager().registerCommand(this, new TempBanCommand("tempban", "snip.ban", new String[] {}));
		getProxy().getPluginManager().registerCommand(this, new KickCommand("kick", "snip.kick", new String[] {}));
		getProxy().getPluginManager().registerCommand(this, new SnipCommand("snip", "snip.snip",
				new String[] { "forbiddance" }));
		getProxy().getPluginManager().registerListener(this, new FEventHandler());
		getProxy().getPluginManager().registerListener(this, new PlayerConnectionHandler());

		logger.info("[Forbiddance] Connected to "
				+ getConfig().getHostname()
				+ " on server id "
				+ String.valueOf(getConfig().getServerID())
				+ "!");
		debug.debug(getClass(), "Connecting to MySQL.");
		pool = new SQLConnectionPool(getConfig().getURL(), getConfig().getUsername(), getConfig().getPassword());
		reader = SQLReader.getManager(this);
		writer = SQLWriter.getManager(this);
		logger.info("[Forbiddance] Finished loading!");

	}

	public void onDisable() {

	}

	public FileManager getFiles() {
		return files;
	}

	public Connection Connection() {
		try {
			final Connection c = pool.getConnection();
			if (!connected) {
				debug.debug(getClass(), "MySQL connection rebuild");
				connected = true;
			}
			return c;
		} catch (final Exception e) {
			if (connected) {
				debug.debug(getClass(), "SQL connection error");
				debug.debug(getClass(), e);
				connected = false;
			} else debug.debug(getClass(), "SQL connection lost");
			return null;
		}
	}

	public static SQLReader getReader() {
		return reader;
	}

	public static SQLWriter getWriter() {
		return writer;
	}

	public static Debugger debug() {
		return debug;
	}

	public static Logger getSnipLogger() {
		return logger;
	}

	public static ConfigurationFile getConfig() {
		return ((ConfigurationFile) files.getFile("configuration"));
	}

	public static String getVersion() {
		return version;
	}
}
