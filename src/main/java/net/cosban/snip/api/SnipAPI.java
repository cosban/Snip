package net.cosban.snip.api;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import net.cosban.snip.Snip;
import net.cosban.snip.events.KickEvent;
import net.cosban.snip.events.UnbanEvent;
import net.cosban.snip.sql.SQLReader;
import net.cosban.snip.sql.SQLWriter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SnipAPI {
	static Logger				logger	= Snip.getSnipLogger();
	private static SQLReader	reader	= Snip.getReader();
	private static SQLWriter	writer	= Snip.getWriter();

	/**
	 * Check if a player is banned from the server.
	 * 
	 * @param player
	 *        ProxiedPlayer
	 * @return True if the player is banned
	 */
	public static boolean isbanned(ProxiedPlayer player) {
		return reader.queryBanState(player);
	}

	/**
	 * Check if a player is banned from the server by name.
	 * 
	 * @param name
	 *        Name of player
	 * @return True if the player is banned
	 */
	public static boolean isbanned(String name) {
		return isbanned(ProxyServer.getInstance().getPlayer(name));
	}

	/**
	 * Check if a player is banned from the server by name.
	 * 
	 * @param name
	 *        Name of player
	 * @return True if the player is banned
	 */
	public static boolean isbanned(UUID uuid) {
		return reader.queryBanState(uuid);
	}

	/**
	 * Check if an address is banned from the server.
	 * 
	 * @param address
	 *        Inet address in texual representative form
	 * @return True if address is banned
	 */
	public static boolean isbanned(InetAddress address) {
		return reader.queryBanState(address);
	}

	/**
	 * Ban a player and kick them if they are on the server.
	 * 
	 * @param player
	 *        ProxiedPlayer to (kick)ban
	 * @return True if the ban was set successfully.
	 */
	public static boolean ban(ProxiedPlayer player, CommandSender sender) {
		return ban(player.getName(), sender);
	}

	/**
	 * Ban a player with a reason and kick them if they are on the server.
	 * 
	 * @param player
	 *        ProxiedPlayer to (kick)ban
	 * @param reason
	 *        The ban reason (and kick reason if the player is online)
	 * @return True if the ban was set successfully.
	 */
	public static boolean ban(ProxiedPlayer player, final String reason, CommandSender sender) {
		return ban(player.getName(), reason, sender);
	}

	/**
	 * Ban a player and kick them if they are on the server.
	 * 
	 * @param name
	 *        ProxiedPlayer to (kick)ban by name
	 * @return True if the ban was set successfully.
	 */
	public static boolean ban(String name, final CommandSender sender) {
		return ban(name, "", sender);
	}

	/**
	 * Ban a player with a reason and kick them if they are on the server.
	 * 
	 * @param name
	 *        ProxiedPlayer to (kick)ban by name
	 * @param reason
	 *        The ban reason (and kick reason if the player is online)
	 * @return True if the ban was set successfully.
	 */
	public static boolean ban(String name, final String reason, final CommandSender sender) {
		writer.queueBan(ProxyServer.getInstance().getPlayer(name), sender.getName(), reason);
		return false;
	}

	/**
	 * Ban an IP address and kick any users connecting from that address if they
	 * are on the server.
	 * 
	 * @param address
	 *        Address to ban in textual representation.
	 * @return True if the ban was set successfully.
	 */
	public static boolean ban(InetAddress address, CommandSender sender) {
		return ban(address, "", sender);
	}

	/**
	 * Ban an IP address with a reason and kick any users connecting from that
	 * address if they are on the server.
	 * 
	 * @param address
	 *        Address to (kick)ban in textual representation.
	 * @param reason
	 *        The ban reason (and kick reason if the player is online)
	 * @return True if the ban was set successfully.
	 */
	public static boolean ban(InetAddress address, final String reason, CommandSender sender) {
		writer.queueBan(address, reason, sender.getName());
		return false;
	}

	public static boolean importban(String name, final String reason, final CommandSender sender, final long timestamp) {
		return false;
	}

	/**
	 * Unban a player from the server (both permanent and temporary bans)
	 * 
	 * @param player
	 *        The player to unban.
	 * @return True if the player was successfully unbanned.
	 */
	public static boolean unban(ProxiedPlayer player, CommandSender sender) {
		ProxyServer.getInstance().getPluginManager().callEvent(new UnbanEvent(player.getName(), sender));
		writer.queueUnban(player, sender.getName());
		return false;
	}

	/**
	 * Unban a player from the server (both permanent and temporary bans)
	 * 
	 * @param name
	 *        The player to unban by name.
	 * @return True if the player was successfully unbanned.
	 */
	public static boolean unban(String name, CommandSender sender) {
		return unban(ProxyServer.getInstance().getPlayer(name), sender);
	}

	/**
	 * Unban an address from the server (both permanent and temporary bans).
	 * 
	 * @param address
	 *        The address to unban in textual representation.
	 * @return True if the address was successfully unbanned.
	 */
	public static boolean unban(InetAddress address, CommandSender sender) {
		ProxyServer.getInstance().getPluginManager().callEvent(new UnbanEvent(address.getHostAddress(), sender));
		writer.queueUnban(address, sender.getName());
		return false;
	}

	/**
	 * Temporarily ban a player for an amount of time.
	 * 
	 * @param player
	 *        The player to ban.
	 * @param seconds
	 *        How long to ban the player for. (in seconds)
	 * @return If the player was successfully temp banned.
	 */
	public static boolean tempban(ProxiedPlayer player, long seconds, CommandSender sender) {
		return tempban(player.getName(), seconds, sender);
	}

	/**
	 * Temporarily ban a player for an amount of time.
	 * 
	 * @param name
	 *        The player to ban by name.
	 * @param seconds
	 *        How long to ban the player for. (in seconds)
	 * @return If the player was successfully temp banned.
	 */
	public static boolean tempban(String name, long seconds, final CommandSender sender) {
		return tempban(name, "", seconds, sender);
	}

	/**
	 * Temporarily ban an address for an amount of time.
	 * 
	 * @param address
	 *        The address to ban in textual representation.
	 * @param seconds
	 *        How long to ban the address for. (in seconds)
	 * @return If the address was successfully temp banned.
	 */
	public static boolean tempban(InetAddress address, long seconds, CommandSender sender) {
		return tempban(address.getHostAddress(), seconds, sender);
	}

	/**
	 * Temporarily ban a player for a reason.
	 * 
	 * @param player
	 *        The player to temporarily ban.
	 * @param reason
	 *        The reason for the ban.
	 * @param seconds
	 *        How long to ban the player for. (in seconds)
	 * @return If the player was successfully banned.
	 */
	public static boolean tempban(ProxiedPlayer player, final String reason, long seconds, String sender) {
		writer.queueTempBan(player, sender, seconds, reason);
		return false;
	}

	/**
	 * Temporarily ban a player for a reason.
	 * 
	 * @param name
	 *        The player to temporarily ban.
	 * @param reason
	 *        The reason for the ban.
	 * @param seconds
	 *        How long to ban the player for. (in seconds)
	 * @return If the player was successfully banned.
	 */
	public static boolean tempban(String name, final String reason, long seconds, final CommandSender sender) {
		return tempban(ProxyServer.getInstance().getPlayer(name), sender.getName(), seconds, reason);
	}

	/**
	 * Temporarily ban an address for a reason.
	 * 
	 * @param address
	 *        The address to temporarily ban in textual representation.
	 * @param reason
	 *        The reason for the ban.
	 * @param seconds
	 *        How long to ban the address for. (in seconds)
	 * @return If the address was successfully banned.
	 */
	public static boolean tempban(InetAddress address, final String reason, long seconds, CommandSender sender) {
		return tempban(address.getHostAddress(), reason, seconds, sender);
	}

	/**
	 * Get the creator of the ban on a player.
	 * 
	 * @param player
	 *        The banned player to look up.
	 * @return The name of the creator of the ban. If not banned, return null.
	 *         If banned by a non-player return SERVER.
	 */
	public static String getCreator(ProxiedPlayer player) {
		return getCreator(player.getName());
	}

	/**
	 * Get the creator of the last ban on a player.
	 * 
	 * @param name
	 *        The banned player to look up.
	 * @return The name of the creator of the ban. If not banned, return null.
	 *         If banned by a non-player return SERVER.
	 */
	public static String getCreator(String name) {
		return reader.queryLastBan(ProxyServer.getInstance().getPlayer(name)).getCreator();
	}

	/**
	 * Get the creator of the last ban on a player.
	 * 
	 * @param address
	 *        The banned player to look up.
	 * @return The name of the creator of the ban. If not banned, return null.
	 *         If banned by a non-player return SERVER.
	 */
	public static String getCreator(InetAddress address) {
		return getCreator(address.getHostAddress());
	}

	/**
	 * Get the reason for the last ban.
	 * 
	 * @param player
	 *        The player in question.
	 * @return The reason the requested player was banned for. If not banned,
	 *         return null.
	 */
	public static String getBanReason(ProxiedPlayer player) {
		return getBanReason(player.getName());
	}

	/**
	 * Get the reason for the last ban.
	 * 
	 * @param name
	 *        The player in question.
	 * @return The reason the requested player was banned for. If not banned,
	 *         return null.
	 */
	public static String getBanReason(String name) {
		return reader.queryLastBan(ProxyServer.getInstance().getPlayer(name)).getReason();
	}

	/**
	 * Get the reason for the last ban.
	 * 
	 * @param address
	 *        The address in question.
	 * @return The reason the requested address was banned for. If not banned,
	 *         return null.
	 */
	public static String getBanReason(InetAddress address) {
		return getBanReason(address.getHostAddress());
	}

	/**
	 * Get the time remaining on a temporary ban.
	 * 
	 * @param player
	 *        The player in question.
	 * @return The number of seconds left until the ban expires. If not banned,
	 *         return 0. If ban is permanent, return -1.
	 */
	public static long getRemainingBanTime(ProxiedPlayer player) {
		return getRemainingBanTime(player.getName());
	}

	/**
	 * Get the time remaining on a temporary ban.
	 * 
	 * @param name
	 *        The player in question.
	 * @return The number of seconds left until the ban expires. If not banned,
	 *         return 0. If ban is permanent, return -1.
	 */
	public static long getRemainingBanTime(String name) {
		return reader.queryLastBan(ProxyServer.getInstance().getPlayer(name)).getRemainingBanTime();
	}

	/**
	 * Get the time remaining on a temporary ban.
	 * 
	 * @param address
	 *        The address in question.
	 * @return The number of seconds left until the ban expires. If not banned,
	 *         return 0. If ban is permanent, return -1.
	 */
	public static long getRemainingBanTime(InetAddress address) {
		return getRemainingBanTime(address.getHostAddress());
	}

	/**
	 * Check if a ban is temporary.
	 * 
	 * @param name
	 *        The name of the player that is banned.
	 * @return True if ban is temporary. Otherwise false.
	 */
	public static boolean isTemporary(String name) {
		return reader.queryLastBan(ProxyServer.getInstance().getPlayer(name)).isTemporary();
	}

	/**
	 * Check if a ban is temporary.
	 * 
	 * @param player
	 *        The name of the player that is banned.
	 * @return True if ban is temporary. Otherwise false.
	 */
	public static boolean isTemporary(ProxiedPlayer player) {
		return isTemporary(player.getName());
	}

	/**
	 * Check if a ban is temporary.
	 * 
	 * @param address
	 *        The address that is banned.
	 * @return True if ban is temporary. Otherwise false.
	 */
	public static boolean isTemporary(InetAddress address) {
		return isTemporary(address.getHostAddress());
	}

	/**
	 * Get a set of all ban keys.
	 * 
	 * @return A set of all bans.
	 */
	public static Set<String> getBans() {
		// return jedis.keys("*");
		return null;
	}

	/**
	 * Duplicate the keys method
	 * 
	 * @return A set of all matches.
	 */
	public static ArrayList<Ban> bannedIPs(String ip) {
		return reader.queryAddressBans(ip);
	}

	/**
	 * Get the time the last ban was created in unix time format. (Milliseconds
	 * since 1 Jan 1970).
	 * 
	 * @param name
	 *        The name of the banned player.
	 * @return The milliseconds the ban was made in unix time.
	 */
	public static long getTimeCreated(String name) {
		return reader.queryLastBan(ProxyServer.getInstance().getPlayer(name)).getBanCreationTime();
	}

	/**
	 * Get the time a ban was created in unix time format. (Milliseconds since 1
	 * Jan, 1970).
	 * 
	 * @param player
	 *        The banned player.
	 * @return The milliseconds the ban was made in unix time.
	 */
	public static long getTimeCreated(ProxiedPlayer player) {
		return getTimeCreated(player.getName());
	}

	/**
	 * Get the time a ban was created in unix time format. (Milliseconds since 1
	 * Jan, 1970).
	 * 
	 * @param address
	 *        The banned address.
	 * @return The milliseconds the ban was made in unix time.
	 */
	public static long getTimeCreated(InetAddress address) {
		return getTimeCreated(address.getHostAddress());
	}

	/**
	 * Get the details of a ban hashkey.
	 * 
	 * @param name
	 *        The banned name to retrieve details for.
	 * @return A map of key:values.
	 */
	public static Map<String, String> getBanDetails(String name) {
		return null;
		// return jedis.hgetAll(name.toLowerCase());
	}

	/**
	 * Kick a player from the server with a reason.
	 * 
	 * @param player
	 *        The player to kick.
	 * @param reason
	 *        The reason to kick with.
	 */
	public static void kickPlayer(ProxiedPlayer player, String reason, CommandSender sender) {
		ProxyServer.getInstance().getPluginManager().callEvent(new KickEvent(player, sender, reason));
		logger.info((SnipAPI.isbanned(player) ? "[KICK/BAN] " : "[KICK] ")
				+ player.getName()
				+ " - Invoker: "
				+ sender.getName()
				+ " - Reason: \""
				+ reason
				+ "\"");
		player.disconnect(new TextComponent(reason));
	}

	/**
	 * Kick a player from the server.
	 * 
	 * @param player
	 *        The player to kick.
	 */
	public static void kickPlayer(ProxiedPlayer player, CommandSender sender) {
		ProxyServer.getInstance().getPluginManager().callEvent(new KickEvent(player, sender, "Kicked by: "
				+ sender.getName()));
		logger.info("[KICK] " + player.getName() + " - Invoker: " + sender.getName());
		player.disconnect(new TextComponent("Kicked by: " + sender.getName()));
	}
}
