package net.cosban.snip.sql;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.cosban.snip.Snip;
import net.cosban.snip.api.Ban;
import net.cosban.snip.api.Ban.BanType;
import net.cosban.snip.files.ConfigurationFile;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SQLWriter extends TimerTask {
	private Snip				plugin;
	private ConfigurationFile	config	= Snip.getConfig();
	private final Queue<Row>	queue	= new LinkedList<Row>();
	private final Lock			lock	= new ReentrantLock();
	private final String		prefix;

	private SQLWriter(Snip instance) {
		plugin = instance;
		prefix = config.getPrefix() != "" ? config.getPrefix() + "_" : "";
		try {
			final Connection c = plugin.getConnection();
			if (c == null) {
				throw new SQLException("Not connected");
			}
			final DatabaseMetaData dbm = c.getMetaData();
			final Statement state = c.createStatement();
			c.setAutoCommit(true);
			// TODO: Table versioning
			verifyTable(dbm, state, prefix + "bans", "(uid INT UNSIGNED AUTO_INCREMENT NOT NULL,"
					+ " timestamp DATETIME NOT NULL,"
					+ " playername varchar(32) NOT NULL,"
					+ " playerid varchar(32) NOT NULL,"
					+ " ip varchar(255) NOT NULL,"
					+ " creator varchar(32) NOT NULL,"
					+ " bantype varchar(15) NOT NULL,"
					+ " lifetime INT UNSIGNED NOT NULL,"
					+ " reason varchar(255) NOT NULL,"
					+ " banned TINYINT(1) NOT NULL DEFAULT 0,"
					+ " updates INT UNSIGNED NOT NULL,"
					+ " PRIMARY KEY (uid))");

			verifyTable(dbm, state, prefix + "kicks", "(uid INT UNSIGNED AUTO_INCREMENT NOT NULL,"
					+ " timeStamp DATETIME NOT NULL,"
					+ " playername varchar(32) NOT NULL,"
					+ " playerid varchar(32) NOT NULL,"
					+ " creator varchar(32) NOT NULL,"
					+ " reason varchar(255) NOT NULL,"
					+ " PRIMARY KEY (uid))");
			state.close();
			c.close();
		} catch (SQLException e) {
			Snip.debug().debug(getClass(), e);
		}
	}

	public static SQLWriter getManager(Snip instance) {
		SQLWriter manager = new SQLWriter(instance);
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			Snip.debug().debug(manager.getClass(), e);
		}
		return manager;
	}

	public void queueBan(ProxiedPlayer recipient, String creator, String reason) {
		queue.add(new BanInstanceRow(recipient.getName(), recipient.getUniqueId().toString(),
				recipient.getAddress().getAddress(), reason, BanType.PERMANENT, creator, false, -1L,
				System.currentTimeMillis(), false));
	}

	public void queueBan(InetAddress address, String reason, String sender) {
		queue.add(new BanInstanceRow(address, reason, sender, true));
	}

	public void queueTempBan(ProxiedPlayer recipient, String creator, long lifetime, String reason) {
		queue.add(new BanInstanceRow(recipient.getName(), recipient.getUniqueId().toString(),
				recipient.getAddress().getAddress(), reason, BanType.TEMPORARY, creator, true, lifetime,
				System.currentTimeMillis(), true));
	}

	public void queueUnban(ProxiedPlayer recipient, String creator) {
		queue.add(new BanInstanceRow(recipient.getName(), recipient.getUniqueId().toString(),
				recipient.getAddress().getAddress(), creator, false));
	}

	public void queueUnban(InetAddress address, String sender) {
		queue.add(new BanInstanceRow(address, "", sender, false));
	}

	public void queueKick(ProxiedPlayer recipient, ProxiedPlayer creator, String reason) {
		queue.add(new KickInstanceRow(recipient, creator, reason));
	}

	@Override
	public void run() {
		if (queue.isEmpty() || !lock.tryLock()) return;
		final Connection c = plugin.getConnection();
		Statement state = null;
		if (queue.size() >= 10000) {
			Snip.debug().debug(getClass(), "Queue overloaded. Size: " + queue.size());
		}
		try {
			if (c == null) return;
			c.setAutoCommit(false);
			state = c.createStatement();
			final long start = System.currentTimeMillis();
			while (!queue.isEmpty() && (System.currentTimeMillis() - start < 1000)) {
				final Row r = queue.poll();
				if (r == null) continue;
				try {
					if (r.toInsert()) {
						state.execute(r.getInsertStatement());
					} else {
						state.execute(r.getUpdateStatement());
					}
				} catch (final SQLException e) {
					Snip.debug().debug(getClass(), e);
					break;
				}
			}
			c.commit();
		} catch (final SQLException e) {
			Snip.debug().debug(getClass(), e);
		} finally {}
	}

	private void verifyTable(DatabaseMetaData dbm, Statement state, String table, String query) throws SQLException {
		if (!dbm.getTables(null, null, table, null).next()) {
			Snip.debug().debug(getClass(), "Creating " + table + " table");
			state.execute("CREATE TABLE `" + table + "` " + query);
			if (!dbm.getTables(null, null, table, null).next())
				throw new SQLException("Table " + table + " not found and failed to create");
		} else {
			Snip.debug().debug(getClass(), "Verified " + table + " table, no need to create");
		}
	}

	private static interface Row {
		boolean toInsert();

		String getInsertStatement();

		String getUpdateStatement();
	}

	private class BanInstanceRow extends Ban implements Row {

		public BanInstanceRow(InetAddress address, String reason, String creator, boolean banned) {
			super(address, reason, creator, banned);
			this.toInsert = banned;
		}

		public BanInstanceRow(String player, String uuid, InetAddress address, String reason, BanType type,
				String creator, boolean temporary, long bantime, long creationtime, boolean banned) {
			super(player, uuid, address, reason, type, creator, temporary, bantime, creationtime, banned);
			this.toInsert = true;
		}

		public BanInstanceRow(String player, String uuid, InetAddress address, String creator, boolean banned) {
			super(player, uuid, address, "", BanType.UNBAN, creator, false, -1L, 0, banned);
			this.toInsert = false;
		}

		private final String	table	= prefix + "bans";
		private boolean			toInsert;

		@Override
		public String getInsertStatement() {
			return "INSERT INTO `"
					+ table
					+ "` (timestamp, playername, playerid, ip, creator, bantype, lifetime, reason, banned, updates) VALUES ("
					+ this.getBanCreationTime()
					+ ", "
					+ this.getPlayerName()
					+ ", "
					+ this.getUUID()
					+ ", "
					+ this.getAddress()
					+ ", "
					+ this.getCreator()
					+ ", "
					+ this.getType()
					+ ", "
					+ this.getBanTime()
					+ ", `"
					+ this.getReason()
					+ "`, "
					+ this.isBanned()
					+ ", 0);";
		}

		@Override
		public String getUpdateStatement() {
			return "UPDATE `" + table + "` SET banned =" + isBanned() + " WHERE playerid=" + getUUID() + ";";
		}

		@Override
		public boolean toInsert() {
			return toInsert;
		}
	}

	private class KickInstanceRow implements Row {
		private final String	table	= prefix + "kicks";
		private ProxiedPlayer	recipient, creator;
		private String			reason;

		public KickInstanceRow(ProxiedPlayer recipient, ProxiedPlayer creator, String reason) {
			this.recipient = recipient;
			this.creator = creator;
			this.reason = reason;
		}

		@Override
		public String getInsertStatement() {
			return "INSERT INTO `"
					+ table
					+ "` (timeStamp, playerid, creator, reason) VALUES ("
					+ System.currentTimeMillis()
					+ ", "
					+ recipient.getUniqueId()
					+ ", "
					+ creator.getName()
					+ ", `"
					+ reason
					+ "`);";
		}

		@Override
		public String getUpdateStatement() {
			return null;
		}

		@Override
		public boolean toInsert() {
			return true;
		}
	}
}
