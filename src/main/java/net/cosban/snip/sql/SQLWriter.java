package net.cosban.snip.sql;

import net.cosban.snip.Snip;
import net.cosban.snip.api.Ban;
import net.cosban.snip.api.Ban.BanType;
import net.cosban.snip.files.ConfigurationFile;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SQLWriter extends TimerTask {
	private final Queue<Row> queue = new LinkedList<Row>();
	private final Lock       lock  = new ReentrantLock();
	private final String prefix;
	private       Snip   plugin;
	private ConfigurationFile config = Snip.getConfig();

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
					+ " timestamp BIGINT UNSIGNED NOT NULL,"
					+ " playername varchar(32) NOT NULL,"
					+ " playerid varchar(36) NOT NULL,"
					+ " ip varchar(255) NOT NULL,"
					+ " creator varchar(32) NOT NULL,"
					+ " bantype varchar(15) NOT NULL,"
					+ " lifetime INT UNSIGNED NOT NULL,"
					+ " reason varchar(255) NOT NULL,"
					+ " banned TINYINT(1) NOT NULL DEFAULT 0,"
					+ " updates INT UNSIGNED NOT NULL,"
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

	public void queueBan(String username, String creator, String reason) {
		queue.add(new BanRow(username, "", "", reason, BanType.PERMANENT, creator, 0L, System.currentTimeMillis()));
	}

	public void queueBan(String username, String uuid, String address, String reason, String creator) {
		queue.add(new BanRow(username, uuid, address, reason, BanType.PERMANENT, creator, 0L,
				System.currentTimeMillis()));
	}

	public void queueIPBan(InetAddress address, String reason, String sender) {
		queue.add(new IPBanRow(address, reason, sender, System.currentTimeMillis()));
	}

	public void queueIPBan(String username, UUID uuid, InetAddress address, String reason, String creator) {
		queue.add(new IPBanRow(username, uuid.toString(), address, reason, creator, System.currentTimeMillis()));
	}

	public void queueAltBan(String username, String uuid, String address, String reason, String creator) {
		queue.add(new BanRow(username, uuid, address, reason, BanType.ALTBAN, creator, 0L,
				System.currentTimeMillis()));
	}

	public void queueTempBan(String username, String uuid, String address, String reason, String creator,
			long duration) {
		queue.add(new BanRow(username, uuid, address, reason, BanType.TEMPORARY, creator, duration,
				System.currentTimeMillis()));
	}

	public void queueUnban(String name, String creator) {
		queue.add(new BanRow(name, "", null, creator));
	}

	public void queueIPUnban(InetAddress address, String sender) {
		queue.add(new IPBanRow(address, sender));
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
						Snip.debug().debug(getClass(), r.getInsertStatement());
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
		} finally {
			try {
				if (state != null) {
					state.close();
				}
				if (c != null) {
					c.close();
				}
			} catch (final SQLException e) {
				Snip.debug().debug(getClass(), e);
			}
			lock.unlock();
		}
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

	private class IPBanRow extends Ban implements Row {
		private boolean toInsert;

		public IPBanRow(String username, String uuid, InetAddress address, String reason, String creator,
				long creation) {
			super(username, uuid, address.getHostAddress(), reason, BanType.IPV4, creator, 0L, creation, true);
			toInsert = true;
		}

		public IPBanRow(InetAddress address, String reason, String creator, long creationtime) {
			super(address.getHostAddress(), reason, creator, creationtime, true);
			this.toInsert = true;
		}

		public IPBanRow(InetAddress address, String creator) {
			super(address.getHostAddress(), "", creator, 0L, false);
			toInsert = false;
		}

		public boolean toInsert() {
			return toInsert;
		}

		@Override
		public String getInsertStatement() {
			return "INSERT INTO `"
					+ table
					+ "` (timestamp, playername, playerid, ip, creator, bantype, lifetime, reason, banned, "
					+
					"updates) VALUES ("
					+ this.getCreationTime()
					+ ", '"
					+ this.getPlayerName()
					+ "', '"
					+ this.getUUID()
					+ "', '"
					+ this.getAddress().getHostAddress()
					+ "', '"
					+ this.getCreator()
					+ "', '"
					+ this.getType()
					+ "', "
					+ this.getDuration()
					+ ", '"
					+ this.getReason()
					+ "', "
					+ this.isBanned()
					+ ", 0);";
		}

		public String getUpdateStatement() {
			return "UPDATE `"
					+ table
					+ "` SET banned ="
					+ isBanned()
					+ " WHERE (ip='"
					+ getAddress().getHostAddress()
					+ "' AND bantype='IPV4');";
		}

		private final String table = prefix + "bans";

	}

	private class BanRow extends Ban implements Row {

		private boolean toInsert;

		public BanRow(String username, String uuid, String address, String reason, BanType type, String creator,
				long duration, long creation) {
			super(username, uuid, address, reason, type, creator, duration, creation, true);
			toInsert = true;
		}

		public BanRow(String player, String uuid, String address, String creator) {
			super(player, uuid, address, "", BanType.UNBAN, creator, 0L, 0L, false);
			this.toInsert = false;
		}

		@Override
		public String getInsertStatement() {
			return "INSERT INTO `"
					+ table
					+ "` (timestamp, playername, playerid, ip, creator, bantype, lifetime, reason, banned, "
					+
					"updates) VALUES ("
					+ this.getCreationTime()
					+ ", '"
					+ this.getPlayerName()
					+ "', '"
					+ this.getUUID()
					+ "', '"
					+ this.getAddress().getHostAddress()
					+ "', '"
					+ this.getCreator()
					+ "', '"
					+ this.getType()
					+ "', "
					+ this.getDuration()
					+ ", '"
					+ this.getReason()
					+ "', "
					+ this.isBanned()
					+ ", 0);";
		}

		@Override
		public String getUpdateStatement() {
			return "UPDATE `" + table + "` SET banned =" + isBanned() + " WHERE playername='" + getPlayerName() + "';";
		}

		@Override
		public boolean toInsert() {
			return toInsert;
		}

		private final String table = prefix + "bans";
	}
}
