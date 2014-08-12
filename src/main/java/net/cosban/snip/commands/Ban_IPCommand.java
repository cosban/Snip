package net.cosban.snip.commands;

import java.net.InetAddress;
import java.net.UnknownHostException;

import net.cosban.snip.api.SnipAPI;
import net.cosban.utils.commands.CommandBase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Ban_IPCommand extends SnipCommand {

	@CommandBase(
			name = "ban-ip",
			params = {},
			description = "Bans a specified, or players IP or range",
			aliases = { "banip", "ipban", "ip-ban" },
			permission = "snip.banip")
	public Ban_IPCommand(String name) {
		super(name);
	}

	public Ban_IPCommand(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
	}

	public void execute(CommandSender sender, String[] args) {
		if (sender.hasPermission("snip.banip") || !(sender instanceof ProxiedPlayer)) {
			if (args.length == 1) {
				if (!SnipAPI.isbanned(args[0])) {
					if (!args[0].matches("[A-Fa-f\\d\\.:]+/[\\d]{1,3}")) {
						sender.sendMessage(new TextComponent(ChatColor.DARK_RED + "Not a valid IP address!"));
						return;
					}
					ban(sender, args[0], "IP banned");
				} else if (args.length >= 2) {
					if (args[0].equals("-p")) {
						ban(sender, ProxyServer.getInstance().getPlayer(args[0]), getMessage(args, 2));
					} else {
						ban(sender, args[0], getMessage(args, 1));
					}
				}
			} else {
				sender.sendMessage(new TextComponent(ChatColor.RED
						+ "Syntax: /ban-ip [-p] <address[/prefixlen]|name> [reason]"));
			}
		} else {
			sender.sendMessage(new TextComponent(ChatColor.RED + "You do not have permission for this command!"));
			SnipAPI.kickPlayer((ProxiedPlayer) sender, ChatColor.DARK_RED
					+ "YOU DO NOT HAVE PERMISSION FOR THIS COMMAND!", sender);
			return;
		}
	}

	private String getMessage(String[] args, int index) {
		String message = "";
		for (int i = index; i < args.length; i++) {
			message += args[i] + " ";
		}
		return message.trim();
	}

	private void ban(CommandSender sender, InetAddress address, String reason) {
		if (!SnipAPI.isbanned(address)) {
			SnipAPI.ban(address, reason, sender);
			for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
				if (address.getHostAddress().equalsIgnoreCase(p.getAddress().getAddress().getHostAddress())) {
					// TODO: fix reason
					SnipAPI.kickPlayer(p, "IP Banned: " + reason, sender);
				}
			}
			sender.sendMessage(new TextComponent(ChatColor.GREEN + address.getHostAddress() + " has been IP banned."));
		} else {
			sender.sendMessage(new TextComponent(ChatColor.RED + address.getHostAddress() + " is already IP banned!"));
		}
	}

	private void ban(CommandSender sender, ProxiedPlayer player, String reason) {
		ban(sender, player.getAddress().getAddress(), reason);
	}

	private void ban(CommandSender sender, String address, String reason) {
		try {
			ban(sender, InetAddress.getByName(address), reason);
		} catch (UnknownHostException e) {
			sender.sendMessage(new TextComponent(ChatColor.DARK_RED + "Unknown host or not a valid IP address!"));
		}
	}
}
