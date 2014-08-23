package net.cosban.snip.commands;

import net.cosban.snip.api.SnipAPI;
import net.cosban.utils.commands.CommandBase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class KickCommand extends SnipCommand {
	@CommandBase(
			name = "kick",
			params = { },
			description = "Kicks a player from bungee",
			aliases = { },
			permission = "snip.kick")
	public KickCommand(String name) {
		super(name);
	}

	public KickCommand(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
	}

	public void execute(CommandSender sender, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(new TextComponent(ChatColor.RED + getSyntax()));
			return;
		}
		if (args.length == 2 && args[0].equalsIgnoreCase("-re")) {
			List<ProxiedPlayer> matchingPlayers = new ArrayList<>();
			try {
				Pattern pattern = Pattern.compile(args[1], Pattern.CASE_INSENSITIVE);
				for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
					if (pattern.matcher(player.getName()).matches()) {
						matchingPlayers.add(player);
					}
				}
			} catch (PatternSyntaxException e) {
				sender.sendMessage(new TextComponent(ChatColor.RED + "Invalid regular expression: " + e.getPattern()));
				return;
			}

			if (matchingPlayers.size() <= 0) {
				sender.sendMessage(new TextComponent(ChatColor.GREEN + "No matching players were found."));
				return;
			}
			for (ProxiedPlayer player : matchingPlayers) {
				SnipAPI.kickPlayer(player.getName(), "RE kick.", sender.getName());
				ProxyServer.getInstance().broadcast(new TextComponent(ChatColor.YELLOW
						+ player.getName()
						+ " was kicked from the server."));
			}
			return;
		}

		ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
		if (player != null) {
			SnipAPI.kickPlayer(player.getName(), "KICKED: " + getMessage(args, 1), sender.getName());
			ProxyServer.getInstance().broadcast(new TextComponent(ChatColor.YELLOW
					+ player.getName()
					+ " was kicked from the server."));
		} else {
			sender.sendMessage(new TextComponent(ChatColor.RED + "There are no players by that name online!"));
		}
	}
}
