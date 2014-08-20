package net.cosban.snip.commands;

import net.cosban.snip.api.SnipAPI;
import net.cosban.snip.api.Util;
import net.cosban.utils.TimeUtils;
import net.cosban.utils.commands.CommandBase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TempBanCommand extends SnipCommand {

	@CommandBase(
			name = "tempban",
			params = { },
			description = "Temporarily bans a player",
			aliases = { },
			permission = "snip.ban")
	public TempBanCommand(String name) {
		super(name);
	}

	public TempBanCommand(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
	}

	public void execute(CommandSender sender, String[] args) {
		if (sender.hasPermission("snip.ban") || !(sender instanceof ProxiedPlayer)) {
			ProxiedPlayer player;
			if (args.length == 2) {
				if (!args[0].matches("[_A-Za-z0-9]{1,16}")) {
					sender.sendMessage(new TextComponent(ChatColor.RED
							+ "Invalid player name specified (1-16 chars of \'A-Za-z0-9_\')"));
					return;
				}
				if ((player = ProxyServer.getInstance().getPlayer(args[0])) != null) {
					SnipAPI.tempban(player, Util.parseTimeSpec(args[1]) * 60, sender);
					SnipAPI.kickPlayer(player, "Temporarily banned for "
							+ TimeUtils.getDurationBreakdown(Util.parseTimeSpec(args[1]) * 60), sender);
					sender.sendMessage(new TextComponent(ChatColor.GREEN
							+ player.getName()
							+ " has been banned for "
							+ TimeUtils.getDurationBreakdown(Util.parseTimeSpec(args[1]) * 60)));
				} else {
					if (!SnipAPI.isbanned(args[0].toLowerCase())) {
						SnipAPI.tempban(args[0], Util.parseTimeSpec(args[1]) * 60, sender);
						sender.sendMessage(new TextComponent(ChatColor.GREEN
								+ args[0]
								+ " has been banned for "
								+ TimeUtils.getDurationBreakdown(Util.parseTimeSpec(args[1]) * 60)));
					} else {
						sender.sendMessage(new TextComponent(ChatColor.RED + args[0] + " is already banned!"));
					}
				}
			} else if (args.length >= 3) {
				if (!args[0].matches("[_A-Za-z0-9]{1,16}")) {
					sender.sendMessage(new TextComponent(ChatColor.RED
							+ "Invalid player name specified (1-16 chars of \'A-Za-z0-9_\')"));
					return;
				}
				String message = "";
				for (int i = 2; i < args.length; i++)
					message += args[i] + " ";
				message = message.trim();
				if ((player = ProxyServer.getInstance().getPlayer(args[0])) != null) {
					SnipAPI.tempban(player, message, Util.parseTimeSpec(args[1]) * 60, sender.getName());
					SnipAPI.kickPlayer(player, "Temp Banned: \"" + message + "\" R: " + TimeUtils.getDurationBreakdown(
							Util.parseTimeSpec(args[1])
									* 60), sender);
					sender.sendMessage(new TextComponent(ChatColor.GREEN
							+ player.getName()
							+ " has been banned for "
							+ TimeUtils.getDurationBreakdown(Util.parseTimeSpec(args[1]) * 60)));
				} else {
					if (!SnipAPI.isbanned(args[0].toLowerCase())) {
						SnipAPI.tempban(args[0], message, Util.parseTimeSpec(args[1]) * 60, sender);
						sender.sendMessage(new TextComponent(ChatColor.GREEN
								+ args[0]
								+ " has been banned for "
								+ TimeUtils.getDurationBreakdown(Util.parseTimeSpec(args[1]) * 60)));
					} else {
						sender.sendMessage(new TextComponent(ChatColor.RED + args[0] + " is already banned!"));
					}
				}
			} else {
				sender.sendMessage(new TextComponent(ChatColor.RED + "Syntax: /tempban <player> <time> [reason]"));
				sender.sendMessage(new TextComponent(ChatColor.RED
						+ "Time is specified in days, hours or minutes. E.g. 8d or 26m."));
			}
		} else {
			sender.sendMessage(new TextComponent(ChatColor.RED + "You do not have permission for this command!"));
			SnipAPI.kickPlayer((ProxiedPlayer) sender, ChatColor.DARK_RED
					+ "YOU DO NOT HAVE PERMISSION FOR THIS COMMAND!", sender);
			return;
		}
	}
}
