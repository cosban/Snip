package net.cosban.snip.commands;

import net.cosban.snip.Snip;
import net.cosban.snip.api.SnipAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Ban_IPCommand extends SnipCommand {

	public Ban_IPCommand(Snip instance, String name, String permission, String[] aliases) {
		super(instance, name, permission, aliases);
	}

	public void execute(CommandSender sender, String[] args) {
		if (sender.hasPermission("snip.banip") || !(sender instanceof ProxiedPlayer)) {
			if (args.length == 1) {
				if (!SnipAPI.isbanned(args[0])) {
					if (!args[0].matches("[A-Fa-f\\d\\.:]+/[\\d]{1,3}")) {
						sender.sendMessage(new TextComponent(ChatColor.DARK_RED + "Not a valid IP address or CIDR!"));
						return;
					}
					SnipAPI.ban(args[0], sender);
					for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
						if (player.getAddress().getAddress().getHostAddress().equalsIgnoreCase(args[0])) {
							SnipAPI.kickPlayer(player, "IP Banned.", sender);
						}
					}
					sender.sendMessage(new TextComponent(ChatColor.GREEN + args[0] + " has been banned."));
				} else {
					sender.sendMessage(new TextComponent(ChatColor.RED + args[0] + " is already banned!"));
				}
			} else if (args.length >= 2) {
				if (!SnipAPI.isbanned(args[0])) {
					String message = "";
					for (int i = 1; i < args.length; i++)
						message += args[i] + " ";
					message = message.trim();
					SnipAPI.ban(args[0], message, sender);
					for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
						if (player.getAddress().getAddress().getHostAddress().equalsIgnoreCase(args[0])) {
							SnipAPI.kickPlayer(player, "Banned: " + message, sender);
						}
					}
					sender.sendMessage(new TextComponent(ChatColor.GREEN + args[0] + " has been banned."));
				} else {
					sender.sendMessage(new TextComponent(ChatColor.RED + args[0] + " is already banned!"));
				}
			} else {
				sender.sendMessage(new TextComponent(ChatColor.RED + "Syntax: /ban-ip <address[/prefixlen]> [reason]"));
			}
		} else {
			sender.sendMessage(new TextComponent(ChatColor.RED + "You do not have permission for this command!"));
			SnipAPI.kickPlayer((ProxiedPlayer) sender, ChatColor.DARK_RED
					+ "YOU DO NOT HAVE PERMISSION FOR THIS COMMAND!", sender);
			return;
		}
	}
}
