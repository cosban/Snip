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
	public static void banAlts(String username, String uuid, String address, final String reason,
			final String sender) {
		if (Snip.isConnected()) {
			writer.queueAltBan(username, uuid, address, reason, sender);
		}
	}

	/**
	 * @param username
	 * 		The player to temporarily banIP.
	 * @param reason
	 * 		The reason for the banIP.
	 * @param sender
	 * 		The name of the command initiator
	 */
	public static void banAlts(String username, final String reason, final String sender) {
		banAlts(username, "", "", reason, sender);
	}

	/**
	 * @param username
	 * 		The player to temporarily banIP.
	 * @param sender
	 * 		The name of the command initiator
	 */
	public static void banAlts(String username, final String sender) {
		banAlts(username, "", sender);
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
		kickPlayer(ProxyServer.getInstance().getPlayer(username), sender);
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
			if (isTemporary(username)) {
				return getRemainingBanTime(username);
			} else {
				return -1L;
			}
		} else {
			return 0L;
		}
	}

	/**
	 * Check if a banIP is temporary.
	 *
	 * @param name
	 * 		The name of the player that is banned.
	 *
	 * @return True if banIP is temporary. Otherwise false.
	 */
	public static boolean isTemporary(String name) {
		if (!Snip.isConnected()) return false;
		Ban last = reader.queryLastBan(name);
		if (last == null) {
			return false;
		}
		return last.isTemporary();
	}

	/**
	 * Check if a banIP is temporary.
	 *
	 * @param uuid
	 * 		The uuid of the player that is banned.
	 *
	 * @return True if banIP is temporary. Otherwise false.
	 */
	public static boolean isTemporary(UUID uuid) {
		Ban last = reader.queryLastBan(uuid);
		if (last == null) {
			return false;
		}
		return last.isTemporary();
	}

	/**
	 * Get the reason for the last banIP.
	 *
	 * @param username
	 * 		The username of the player in question.
	 *
	 * @return The reason the requested player was banned for. If not banned, return null.
	 */
	public static String getBanReason(String username) {
		return reader.queryLastBan(username).getReason();
	}

	/**
	 * Get the reason for the last banIP.
	 *
	 * @param uuid
	 * 		The uuid of the player in question.
	 *
	 * @return The reason the requested player was banned for. If not banned, return null.
	 */
	public static String getBanReason(UUID uuid) {
		return reader.queryLastBan(uuid).getReason();
	}

	/**
	 * Get the reason for the last banIP.
	 *
	 * @param address
	 * 		The address in question.
	 *
	 * @return The reason the requested address was banned for. If not banned, return null.
	 */
	public static String getBanReason(InetAddress address) {
		return getBanReason(address.getHostAddress());
	}

	/**
	 * Get the sender of the last banIP on a player.
	 *
	 * @param name
	 * 		The username of the banned player to look up.
	 *
	 * @return The name of the sender of the banIP. If not banned, return null. If banned by a non-player return
	 * SERVER.
	 */
	public static String getCreator(String name) {
		return reader.queryLastBan(name).getCreator();
	}

	/**
	 * Get the sender of the last banIP on a player.
	 *
	 * @param uuid
	 * 		The uuid of the banned player to look up.
	 *
	 * @return The name of the sender of the banIP. If not banned, return null. If banned by a non-player return
	 * SERVER.
	 */
	public static String getCreator(UUID uuid) {
		return reader.queryLastBan(uuid).getCreator();
	}

	/**
	 * Get the sender of the last banIP on a player.
	 *
	 * @param address
	 * 		The banned player to look up.
	 *
	 * @return The name of the sender of the banIP. If not banned, return null. If banned by a non-player return
	 * SERVER.
	 */
	public static String getCreator(InetAddress address) {
		return getCreator(address.getHostAddress());
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
			if(b!= null) {
				if (latest == null || latest.getCreationTime() < b.getCreationTime()) {
					latest = b;
				}
			}
		}
		return latest;
	}
}
