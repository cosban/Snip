package net.cosban.snip.api;

import net.cosban.snip.Snip;
import net.cosban.snip.events.KickEvent;
import net.cosban.snip.events.UnbanEvent;
import net.cosban.snip.sql.SQLReader;
import net.cosban.snip.sql.SQLWriter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

public class SnipAPI {
	static         Logger    logger = Snip.getSnipLogger();
	private static SQLReader reader = Snip.getReader();
	private static SQLWriter writer = Snip.getWriter();

	/**
	 * Check if a player is banned from the server by name.
	 *
	 * @param name
	 * 		Name of player
	 *
	 * @return True if the player is banned
	 */
	public static boolean isBanned(String name) {
		return Snip.isConnected() && reader.queryBanState(name);
	}

	/**
	 * Check if a player is banned from the server by name.
	 *
	 * @param uuid
	 * 		UUID of player
	 *
	 * @return True if the player is banned
	 */
	public static boolean isBanned(UUID uuid) {
		return Snip.isConnected() && reader.queryBanState(uuid);
	}

	/**
	 * Check if an address is banned from the server.
	 *
	 * @param address
	 * 		Inet address in textual representative form
	 *
	 * @return True if address is banned
	 */
	public static boolean isBanned(InetAddress address) {
		return Snip.isConnected() && reader.queryBanState(address);
	}

	/**
	 * @param username
	 * 		The player to temporarily banIP
	 * @param uuid
	 * 		The UUID of the player in string form
	 * @param address
	 * 		Address to banIP in textual representation.
	 * @param reason
	 * 		The banIP reason (and kick reason if the player is online)
	 * @param sender
	 * 		The name of the command initiator
	 */
	public static void ban(String username, String uuid, String address, String reason, String sender) {
		if (Snip.isConnected()) {
			Snip.getWriter().queueBan(username, uuid, address, reason, sender);
		}
	}

	/**
	 * @param username
	 * 		The player to temporarily banIP
	 * @param uuid
	 * 		The UUID of the player in string form
	 * @param address
	 * 		Address to banIP in textual representation.
	 * @param sender
	 * 		The name of the command initiator
	 */
	public static void ban(String username, String uuid, String address, String sender) {
		ban(username, uuid, address, "Banned by " + sender + " for breaking the rules.", sender);
	}

	/**
	 * @param username
	 * 		The player to temporarily banIP
	 * @param reason
	 * 		The banIP reason (and kick reason if the player is online)
	 * @param sender
	 * 		The name of the command initiator
	 */
	public static void ban(String username, String reason, String sender) {
		ban(username, "", "", reason, sender);
	}

	/**
	 * @param username
	 * 		The player to temporarily banIP
	 * @param sender
	 * 		The name of the command initiator
	 */
	public static void ban(String username, String sender) {
		ban(username, "", "", "Banned by " + sender + " for breaking the rules.", sender);
	}

	/**
	 * Ban an IP address and kick any users connecting from that address if they are on the server.
	 *
	 * @param address
	 * 		Address to banIP in textual representation.
	 *
	 * @return True if the banIP was set successfully.
	 */
	public static void banIP(InetAddress address, final String sender) {
		banIP(address, "Banned by: " + sender + " for breaking the rules.", sender);
	}

	/**
	 * Ban an IP address with a reason and kick any users connecting from that address if they are on the server.
	 *
	 * @param address
	 * 		Address to (kick)banIP in textual representation.
	 * @param reason
	 * 		The banIP reason (and kick reason if the player is online)
	 * @param sender
	 * 		The name of the command initiator
	 */
	public static void banIP(InetAddress address, final String reason, final String sender) {
		if (Snip.isConnected()) {
			writer.queueIPBan(address, reason, sender);
		}
	}

	public static void banIP(String username, UUID uuid, InetAddress address, final String reason,
			final String creator) {
		if (Snip.isConnected()) {
			writer.queueIPBan(username, uuid, address, reason, creator);
		}
	}

	/**
	 * Bans the main account and any other alternate accounts which may try to log in. This is more severe than an IP
	 * banIP due to the fact that it works much like a Kill On Site (KOS) order for any alts and may span out.
	 *
	 * @param username
	 * 		The player to temporarily banIP.
	 * @param uuid
	 * 		The UUID of the player in string form
	 * @param address
	 * 		The ip addres of the player
	 * @param reason
	 * 		The reason for the banIP.
	 * @param sender
	 * 		The name of the command initiator
	 */
	public static void banAlts(String username, UUID uuid, InetAddress address, final String reason,
			final String sender) {
		if (Snip.isConnected()) {
			writer.queueAltBan(username, uuid, address, reason, sender);
		}
	}

	/**
	 * Unban a player from the server (both permanent and temporary bans)
	 *
	 * @param name
	 * 		The name of the player to unban.
	 * @param sender
	 * 		The name of the command initiator
	 */
	public static void unban(String name, final String sender) {
		ProxyServer.getInstance().getPluginManager().callEvent(new UnbanEvent(name, sender));
		if (Snip.isConnected()) {
			writer.queueUnban(name, sender);
		}
	}

	/**
	 * Unban an address from the server.
	 *
	 * @param address
	 * 		The address to unban in textual representation.
	 * @param sender
	 * 		The name of the command initiator
	 */
	public static void unban(InetAddress address, final String sender) {
		ProxyServer.getInstance().getPluginManager().callEvent(new UnbanEvent(address.getHostAddress(), sender));
		if (Snip.isConnected()) {
			writer.queueIPUnban(address, sender);
		}
	}

	/**
	 * Temporarily bans a player
	 *
	 * @param name
	 * 		The player to banIP by name.
	 * @param sender
	 * 		The name of the command initiator
	 * @param duration
	 * 		How long to banIP the player for. (in seconds)
	 */
	public static void tempban(String name, final String sender, final long duration) {
		tempban(name, "", "", "", sender, duration);
	}

	/**
	 * Temporarily bans a player
	 *
	 * @param name
	 * 		The player to banIP by name.
	 * @param reason
	 * 		The reason for the banIP.
	 * @param sender
	 * 		The name of the command initiator
	 * @param duration
	 * 		How long to banIP the player for. (in seconds)
	 */
	public static void tempban(String name, String reason, final String sender, final long duration) {
		tempban(name, "", "", reason, sender, duration);
	}

	/**
	 * @param username
	 * 		The player to temporarily banIP.
	 * @param uuid
	 * 		The UUID of the player in string form
	 * @param address
	 * 		The ip addres of the player
	 * @param reason
	 * 		The reason for the banIP.
	 * @param sender
	 * 		The name of the command initiator
	 * @param duration
	 * 		How long to banIP the player for. (in seconds)
	 */
	public static void tempban(String username, String uuid, String address, final String reason, final String sender,
			final long duration) {
		if (Snip.isConnected()) {
			writer.queueTempBan(username, uuid, address, reason, sender, duration);
		}
	}

	public static void kickPlayer(String username, final String sender) {
		kickPlayer(username, "Kicked by: " + sender, sender);
	}

	public static void kickPlayer(String username, String reason, final String sender) {
		kickPlayer(ProxyServer.getInstance().getPlayer(username), reason, sender);
	}

	/**
	 * Kick a player from the server.
	 *
	 * @param player
	 * 		The player to kick.
	 */
	public static void kickPlayer(ProxiedPlayer player, final String sender) {
		kickPlayer(player, "Kicked by: " + sender, sender);
	}

	/**
	 * Kick a player from the server.
	 *
	 * @param player
	 * 		The player to kick.
	 * @param reason
	 * 		The reason to kick with.
	 */
	public static void kickPlayer(ProxiedPlayer player, String reason, final String sender) {
		ProxyServer.getInstance().getPluginManager().callEvent(new KickEvent(player.getName(), sender, reason));
		logger.info((SnipAPI.isBanned(player.getUniqueId()) ? "[KICK/BAN] " : "[KICK] ")
				+ player.getName()
				+ " - Invoker: "
				+ sender
				+ " - Reason:"
				+ reason);
		player.disconnect(new TextComponent(reason));
	}

	/**
	 * Get the time remaining on a temporary banIP.
	 *
	 * @param username
	 * 		The name of the player in question.
	 *
	 * @return The number of seconds left until the banIP expires. If not banned, return 0. If banIP is permanent,
	 * return -1.
	 */
	public static long getRemainingBanTime(String username) {
		if (isBanned(username)) {
			if (getLastBan(username).isTemporary()) {
				//TODO: endless recursion is bad
				return getRemainingBanTime(username);
			} else {
				return -1L;
			}
		} else {
			return 0L;
		}
	}

	/**
	 * Get the time remaining on a temporary banIP.
	 *
	 * @param uuid
	 * 		The name of the player in question.
	 *
	 * @return The number of seconds left until the banIP expires. If not banned, return 0. If banIP is permanent,
	 * return -1.
	 */
	public static long getRemainingBanTime(UUID uuid) {
		if (isBanned(uuid)) {
			if (getLastBan(uuid).isTemporary()) {
				//TODO: endless recursion loop
				return getRemainingBanTime(uuid);
			} else {
				return -1L;
			}
		} else {
			return 0L;
		}
	}

	public static Ban getLastBan(String username) {
		return reader.queryLastBan(username);
	}

	public static Ban getLastBan(UUID uuid) {
		return reader.queryLastBan(uuid);
	}

	public static Ban getLastBan(InetAddress address) {
		return reader.queryLastBan(address);
	}

	/**
	 * @return A set of all matches.
	 */
	public static ArrayList<Ban> getMatchingIPBans(String ip) {
		return reader.queryAddressBans(ip);
	}

	public static Ban getLatestBan(String username, UUID uuid, InetAddress address) {
		ArrayList<Ban> bans = new ArrayList<>();
		bans.add(reader.queryLastBan(username));
		bans.add(reader.queryLastBan(uuid));
		bans.add(reader.queryLastBan(address));
		Ban latest = null;
		for (Ban b : bans) {
			if (b != null) {
				if (latest == null || latest.getCreationTime() < b.getCreationTime()) {
					latest = b;
				}
			}
		}
		return latest;
	}
}
