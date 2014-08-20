package net.cosban.snip.commands;

import net.cosban.snip.api.SnipAPI;
import net.cosban.utils.TimeUtils;
import net.cosban.utils.commands.CommandBase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Map;

public class LookupCommand extends SnipCommand {
	@CommandBase(
			name = "lookup",
			params = { },
			description = "Looks up whether a player or IP is banned",
			aliases = { },
			permission = "snip.lookup")
	public LookupCommand(String name) {
		super(name);
	}

	public LookupCommand(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
	}

	public void execute(CommandSender sender, String[] args) {
		if (sender.hasPermission("snip.lookup") || !(sender instanceof ProxiedPlayer)) {
			if (args.length == 1) {
				if (SnipAPI.isbanned(args[0])) {
					Map<String, String> ban = SnipAPI.getBanDetails(args[0]);

					sender.sendMessage(new TextComponent(ChatColor.YELLOW + "Ban details for " + args[0]));
					sender.sendMessage(new TextComponent(ChatColor.DARK_AQUA
							+ "  Type: "
							+ ChatColor.DARK_RED
							+ ban.get("TYPE")));
					sender.sendMessage(new TextComponent(ChatColor.DARK_AQUA
							+ "  Reason: "
							+ ChatColor.GRAY
							+ ban.get("REASON")));
					sender.sendMessage(new TextComponent(ChatColor.DARK_AQUA
							+ "  Creator: "
							+ ChatColor.YELLOW
							+ ban.get("CREATOR")));
					sender.sendMessage(new TextComponent(ChatColor.DARK_AQUA
							+ "  Timestamp: "
							+ ChatColor.AQUA
							+ TimeUtils.millisToDate(Long.valueOf(ban.get("TIMESTAMP")))));
					if (SnipAPI.isTemporary(args[0])) {
						sender.sendMessage(new TextComponent(ChatColor.DARK_AQUA
								+ "  Remaining: "
								+ ChatColor.BLUE
								+ TimeUtils.getDurationBreakdown(SnipAPI.getRemainingBanTime(args[0]))));
					}
				} else {
					sender.sendMessage(new TextComponent(ChatColor.RED + args[0] + " is not banned!"));
				}
			} else {
				sender.sendMessage(new TextComponent(ChatColor.RED + "Syntax: /lookup <player>"));
			}
		} else {
			sender.sendMessage(new TextComponent(ChatColor.RED + "You do not have permission for this command!"));
			SnipAPI.kickPlayer((ProxiedPlayer) sender, ChatColor.DARK_RED
					+ "YOU DO NOT HAVE PERMISSION FOR THIS COMMAND!", sender);
		}
	}
}
