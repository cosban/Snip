package net.cosban.snip.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.cosban.snip.api.SnipAPI;
import net.cosban.utils.TimeUtils;
import net.cosban.utils.commands.CommandBase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ListBansCommand extends SnipCommand {

	@CommandBase(
			name = "listbans",
			params = {},
			description = "Lists all active bans",
			aliases = {},
			permission = "snip.listbans")
	public ListBansCommand(String name) {
		super(name);
	}

	public ListBansCommand(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
	}

	public void execute(CommandSender sender, String[] args) {
		if (sender.hasPermission("snip.listbans") || !(sender instanceof ProxiedPlayer)) {
			if (args.length == 1) {
				List<String> matchingbans = new ArrayList<String>();
				try {
					Pattern regex = Pattern.compile(args[0], Pattern.CASE_INSENSITIVE);

					for (String name : SnipAPI.getBans()) {
						if (regex.matcher(name).matches()) {
							matchingbans.add(name);
						}
					}
				} catch (PatternSyntaxException e) {
					sender.sendMessage(new TextComponent(ChatColor.RED
							+ "Invalid regular expression: "
							+ e.getPattern()));
				}

				if (matchingbans.size() == 1) {
					String player = matchingbans.get(0);
					Map<String, String> ban = SnipAPI.getBanDetails(player);

					sender.sendMessage(new TextComponent(ChatColor.YELLOW + "Ban details for " + player));
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
					if (SnipAPI.isTemporary(player)) {
						sender.sendMessage(new TextComponent(ChatColor.DARK_AQUA
								+ "  Remaining: "
								+ ChatColor.BLUE
								+ TimeUtils.getDurationBreakdown(SnipAPI.getRemainingBanTime(player))));
					}
				} else if (matchingbans.size() > 0) {
					Collections.sort(matchingbans);
					for (String ban : matchingbans) {
						Map<String, String> details = SnipAPI.getBanDetails(ban);
						sender.sendMessage(new TextComponent(ChatColor.DARK_RED
								+ (details.get("TYPE").equals("TEMPORARY") ? "T: " : "P: ")
								+ ChatColor.YELLOW
								+ ban
								+ ChatColor.DARK_AQUA
								+ " for "
								+ ChatColor.GRAY
								+ "\""
								+ details.get("REASON")
								+ "\""
								+ ChatColor.DARK_AQUA
								+ " by "
								+ ChatColor.YELLOW
								+ details.get("CREATOR")
								+ ChatColor.DARK_AQUA
								+ " at "
								+ ChatColor.AQUA
								+ TimeUtils.millisToDate(Long.valueOf(details.get("TIMESTAMP")))));
					}
					sender.sendMessage(new TextComponent(ChatColor.GREEN
							+ String.valueOf(matchingbans.size())
							+ " matching bans were found."));
				} else {
					sender.sendMessage(new TextComponent(ChatColor.RED + "No matching bans were found."));
					return;
				}
				return;
			} else {
				sender.sendMessage(new TextComponent(ChatColor.RED + "Syntax: /listbans <regex>"));
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
