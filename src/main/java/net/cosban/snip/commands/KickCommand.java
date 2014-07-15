package net.cosban.snip.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.cosban.snip.api.SnipAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class KickCommand extends SnipCommand {

	public KickCommand(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
	}

	public void execute(CommandSender sender, String[] args) {
		if (sender.hasPermission("snip.kick") || !(sender instanceof ProxiedPlayer)) {
			if (args.length == 1) {
				ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
				SnipAPI.kickPlayer(player, sender);
				ProxyServer.getInstance().broadcast(new TextComponent(ChatColor.YELLOW
						+ player.getName()
						+ " was kicked from the server."));
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("-re")) {
					List<ProxiedPlayer> matchingPlayers = new ArrayList<ProxiedPlayer>();
					try {
						Pattern pattern = Pattern.compile(args[1], Pattern.CASE_INSENSITIVE);

						for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
							if (pattern.matcher(player.getName()).matches()) {
								matchingPlayers.add(player);
							}
						}
					} catch (PatternSyntaxException e) {
						sender.sendMessage(new TextComponent(ChatColor.RED
								+ "Invalid regular expression: "
								+ e.getPattern()));
						return;
					}

					if (matchingPlayers.size() <= 0) {
						sender.sendMessage(new TextComponent(ChatColor.GREEN + "No matching players were found."));
						return;
					} else {
						sender.sendMessage(new TextComponent(matchingPlayers.size() + " matching players were found."));
					}

					for (ProxiedPlayer player : matchingPlayers) {
						SnipAPI.kickPlayer(player, "RE kick.", sender);
						ProxyServer.getInstance().broadcast(new TextComponent(ChatColor.YELLOW
								+ player.getName()
								+ " was kicked from the server - RE Kick."));
					}
					return;
				}
			} else if (args.length >= 2) {
				ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
				String message = "";
				for (int i = 1; i < args.length; i++)
					message += args[i] + " ";
				message = message.trim();
				SnipAPI.kickPlayer(player, message, sender);
				ProxyServer.getInstance().broadcast(new TextComponent(ChatColor.YELLOW
						+ player.getName()
						+ " was kicked from the server - "
						+ message));
				return;
			} else {
				sender.sendMessage(new TextComponent(ChatColor.RED + "Syntax: /kick [-re] <player> [reason]"));
				return;
			}
		} else {
			sender.sendMessage(new TextComponent(ChatColor.RED + "You do not have permission for this command!"));
			SnipAPI.kickPlayer((ProxiedPlayer) sender, ChatColor.DARK_RED
					+ "YOU DO NOT HAVE PERMISSION FOR THIS COMMAND!", sender);
			return;
		}
	}
}
