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
		if (args.length < 2) {
			sender.sendMessage(new TextComponent(getSyntax()));
			return;
		}
		if (!args[0].matches("[_A-Za-z0-9]{1,16}")) {
			sender.sendMessage(new TextComponent(ChatColor.RED
					+ "Invalid player name specified (1-16 chars of \'A-Za-z0-9_\')"));
			return;
		}

		ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
		if (player != null) {
			SnipAPI.tempban(player.getName(), player.getUniqueId().toString(), player.getAddress().getAddress()
							.getHostAddress(),
					"TEMP BANNED: "
							+ getMessage(args, 2), sender.getName(), Util.parseTimeSpec(args[1]) * 60);
			SnipAPI.kickPlayer(player.getName(), "TEMP BANNED UNTIL: "
					+ TimeUtils.getDurationBreakdown(Util.parseTimeSpec(args[1]) * 60)
					+ " FOR: "
					+ getMessage(args, 2), sender.getName());
			sender.sendMessage(new TextComponent(ChatColor.GREEN
					+ player.getName()
					+ " has been banned until "
					+ TimeUtils.getDurationBreakdown(Util.parseTimeSpec(args[1]) * 60)));
		} else {
			if (!SnipAPI.isBanned(args[0].toLowerCase())) {
				SnipAPI.tempban(args[0], sender.getName(), Util.parseTimeSpec(args[1]) * 60);
				sender.sendMessage(new TextComponent(ChatColor.GREEN
						+ args[0]
						+ " has been banned for "
						+ TimeUtils.getDurationBreakdown(Util.parseTimeSpec(args[1]) * 60)));
			} else {
				sender.sendMessage(new TextComponent(ChatColor.RED + args[0] + " is already banned!"));
			}
		}
	}
}
