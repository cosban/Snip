package net.cosban.snip;

import java.sql.Connection;
import java.util.concurrent.TimeUnit;
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
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class Snip extends Plugin {
	private static String		version;
	private static Debugger		debug;
	private static FileManager	files;
	private static SQLReader	reader;
	private static SQLWriter	writer;
	private static boolean		connected;
	private SQLConnectionPool	pool;

	protected static Logger		logger;

	public void onEnable() {
		version = getDescription().getVersion();
		files = new FileManager(this);
		debug = new Debugger(getClass().getName(), getConfig().toUseDebug());
		debug.setLogger(getLogger());
		debug.debug(getClass(), "THE DEBUGGER IS ENABLED AND MAY BE VERY SPAMMY. THIS IS YOUR ONLY WARNING.");
		logger = getLogger();

		getProxy().getPluginManager().registerCommand(this, new BanCommand(this, "ban", "snip.ban", new String[] {}));
		getProxy().getPluginManager().registerCommand(this, new UnbanCommand(this, "unban", "snip.unban",
				new String[] {}));
		getProxy().getPluginManager().registerCommand(this, new Ban_IPCommand(this, "ban-ip", "snip.banip",
				new String[] { "banip", "ipban", "ip-ban" }));
		getProxy().getPluginManager().registerCommand(this, new Unban_IPCommand(this, "unban-ip", "snip.unbanip",
				new String[] { "unbanip", "ipunban", "ip-unban" }));
		getProxy().getPluginManager().registerCommand(this, new LookupCommand(this, "lookup", "snip.lookup",
				new String[] {}));
		getProxy().getPluginManager().registerCommand(this, new ListBansCommand(this, "listbans", "snip.listbans",
				new String[] {}));
		getProxy().getPluginManager().registerCommand(this, new TempBanCommand(this, "tempban", "snip.ban",
				new String[] {}));
		getProxy().getPluginManager().registerCommand(this, new KickCommand(this, "kick", "snip.kick", new String[] {}));
		getProxy().getPluginManager().registerCommand(this, new SnipCommand(this, "snip", "snip.snip",
				new String[] { "forbiddance" }));
		getProxy().getPluginManager().registerListener(this, new FEventHandler());
		getProxy().getPluginManager().registerListener(this, new PlayerConnectionHandler());

		debug.debug(getClass(), "Connecting to MySQL... " + getConfig().getUsername() + "@" + getConfig().getURL());
		try {
			pool = new SQLConnectionPool(getConfig().getURL(), getConfig().getUsername(), getConfig().getPassword());
			Connection c = getConnection();
			if (c == null) {
				connected = false;
			} else {
				debug.debug(getClass(), "Connected to MySQL database.");
			}
			ProxyServer.getInstance().getScheduler().runAsync(this, pool.getCloser());
			reader = SQLReader.getManager(this);
			writer = SQLWriter.getManager(this);
			ProxyServer.getInstance().getScheduler().schedule(this, writer, 1, 1, TimeUnit.SECONDS);
		} catch (ClassNotFoundException e) {
			connected = false;
			debug.debug(getClass(), e);
		}
		if (!connected) {
			logger.warning("There was an issue connecting to the MySQL server.");
			logger.warning("It is HIGHLY encouraged that you fix your database connection!");
		}
	}

	public void onDisable() {
		ProxyServer.getInstance().getScheduler().cancel(this);
		pool.close();
	}

	public FileManager getFiles() {
		return files;
	}

	public static boolean isConnected() {
		return connected;
	}

	public Connection getConnection() {
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
				connected = false;
			} else {
				debug.debug(getClass(), "SQL connection lost");
			}
			debug.debug(getClass(), e);
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
