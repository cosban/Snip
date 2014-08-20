package net.cosban.snip.sql;

import net.cosban.snip.Snip;
import net.cosban.snip.api.Ban;
import net.cosban.snip.api.Ban.BanType;
import net.cosban.snip.files.ConfigurationFile;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

public class SQLReader {
	private final String prefix;
	private final String bansTable;
	private Snip plugin;
	private ConfigurationFile config = Snip.getConfig();

	private SQLReader(Snip instance) {
		plugin = instance;
		prefix = config.getPrefix() != "" ? config.getPrefix() + "_" : "";
		bansTable = prefix + "bans";
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
			if (latest == null || b.getBanCreationTime() > latest.getBanCreationTime()) {
				latest = b;
			}
		}
		return latest;
	}

	public ArrayList<Ban> queryBans(ProxiedPlayer p) {
		return runBanQuery("SELECT * FROM `" + bansTable + "` WHERE (playerid='" + p.getUniqueId().toString() + "');");
	}

	public ArrayList<Ban> queryAddressBans(String ip) {
		return runBanQuery("SELECT * FROM `" + bansTable + "` WHERE (ip='" + ip + "');");
	}

	public boolean queryBanState(ProxiedPlayer p) {
		return isBanned(runBanQuery("SELECT * FROM `"
				+ bansTable
				+ "` WHERE (playerid='"
				+ p.getUniqueId().toString()
				+ "');"));
	}

	public boolean queryBanState(String name) {
		return isBanned(runBanQuery("SELECT * FROM `" + bansTable + "` WHERE (playername='" + name + "');"));
	}

	public boolean queryBanState(InetAddress address) {
		return isBanned(runBanQuery("SELECT * FROM `"
				+ bansTable
				+ "` WHERE (ip='"
				+ address.getHostAddress()
				+ "');"));
	}

	public boolean queryBanState(UUID uuid) {
		return isBanned(runBanQuery("SELECT * FROM `" + bansTable + "` WHERE (playerid='" + uuid.toString() + "');"));
	}

	public long queryBanLifetime(ProxiedPlayer p) {
		return getLifeTime(runBanQuery("SELECT * FROM `"
				+ bansTable
				+ "` WHERE (playerid='"
				+ p.getUniqueId().toString()
				+ "');"));
	}

	public ArrayList<Ban> runBanQuery(String query) {
		final Connection c = plugin.getConnection();
		try {
			Statement state = c.createStatement();
			ArrayList<Ban> bans = bansToList(state.executeQuery(query));
			state.close();
			c.close();
			return bans;
		} catch (SQLException e) {
			Snip.debug().debug(getClass(), e);
			return null;
		}
	}

	private ArrayList<Ban> bansToList(ResultSet rs) {
		try {
			ArrayList<Ban> bans = new ArrayList<>();
			while (rs.next()) {
				BanType t = BanType.valueOf(rs.getString("bantype"));
				bans.add(new Ban(rs.getString("playername"), rs.getString("playerid"), InetAddress.getByName(rs.getString("ip")), rs.getString("reason"), t, rs.getString("creator"), rs.getLong("lifetime"), rs.getLong("timestamp"), rs.getBoolean("banned")));
			}
			return bans;
		} catch (SQLException | UnknownHostException e) {
			Snip.debug().debug(getClass(), e);
			return null;
		}
	}

	private boolean isBanned(ArrayList<Ban> bans) {
		for (Ban b : bans) {
			if (b.isBanned()) return true;
		}
		return false;
	}

	private long getLifeTime(ArrayList<Ban> bans) {
		for (Ban b : bans) {
			if (b.isBanned()) {
				return b.getRemainingBanTime();
			}
		}
		return 0L;
	}
}
