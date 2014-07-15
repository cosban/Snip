package net.cosban.snip.sql;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

import net.cosban.snip.Snip;
import net.cosban.snip.api.Ban;
import net.cosban.snip.files.ConfigurationFile;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SQLReader {
	private Snip				plugin;
	private ConfigurationFile	config	= Snip.getConfig();
	private final String		prefix;
	private final String		bansTable;
	private final String		kicksTable;

	private SQLReader(Snip instance) {
		plugin = instance;
		prefix = config.getPrefix() != "" ? config.getPrefix() + "_" : "";
		bansTable = prefix + "bans";
		kicksTable = prefix + "kicks";
	}

	public static SQLReader getManager(Snip instance) {
		SQLReader manager = new SQLReader(instance);
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			Snip.debug().debug(manager.getClass(), e);
		}
		return manager;
	}

	public Ban queryLastBan(ProxiedPlayer p) {
		Ban latest = null;
		for (Ban b : queryBans(p)) {
			if (latest == null || b.getBanTime() > latest.getBanTime()) {
				latest = b;
			}
		}
		return latest;
	}

	public ArrayList<Ban> queryBans(ProxiedPlayer p) {
		return bansToList(p, runQuery("SELECT timestamp, creator, lifetime, reason FROM `"
				+ bansTable
				+ "` WHERE playerid="
				+ p.getUniqueId()
				+ ";"));

	}

	public boolean queryBanState(ProxiedPlayer p) {
		return isBanned(runQuery("SELECT banned FROM `"
				+ bansTable
				+ "` WHERE playerid="
				+ p.getUniqueId().toString()
				+ ";"));
	}

	public boolean queryBanState(InetAddress address) {
		return isBanned(runQuery("SELECT banned FROM `" + bansTable + "` WHERE ip=" + address.getHostAddress() + ";"));
	}

	public boolean queryBanState(UUID uuid) {
		return isBanned(runQuery("SELECT banned FROM `" + bansTable + "` WHERE playerid=" + uuid.toString() + ";"));
	}

	public long queryBanLifetime(ProxiedPlayer p) {
		return getLifeTime(runQuery("SELECT timestamp, lifetime, banned FROM `"
				+ bansTable
				+ "` WHERE playerid="
				+ p.getUniqueId().toString()));
	}

	public ResultSet queryKicks(ProxiedPlayer p) {
		return runQuery("SELECT timestamp, creator, reason FROM `"
				+ kicksTable
				+ "` WHERE playerid="
				+ p.getUniqueId().toString());
	}

	public ResultSet runQuery(String query) {
		final Connection c = plugin.Connection();
		Statement state;
		try {
			state = c.createStatement();
			return state.executeQuery(query);
		} catch (SQLException e) {
			Snip.debug().debug(getClass(), e);
			return null;
		}
	}

	private ArrayList<Ban> bansToList(ProxiedPlayer p, ResultSet rs) {
		try {
			ArrayList<Ban> bans = new ArrayList<>();
			while (rs.next()) {
				bans.add(new Ban(name, uuid, address, reason, type, creator, temporary, duration, timestamp, banned));
			}
			return bans;
		} catch (SQLException e) {
			Snip.debug().debug(getClass(), e);
			return null;
		}
	}

	private boolean isBanned(ResultSet rs) {
		try {
			while (rs.next()) {
				if (rs.getBoolean("banned")) {
					return true;
				}
			}
			return false;
		} catch (SQLException e) {
			Snip.debug().debug(getClass(), e);
			return false;
		}
	}

	private long getLifeTime(ResultSet rs) {
		try {
			while (rs.next()) {
				if (rs.getBoolean("banned")) {
					return rs.getLong("lifetime");
				}
			}
			return 0L;
		} catch (SQLException e) {
			Snip.debug().debug(getClass(), e);
			return 0L;
		}
	}
}
