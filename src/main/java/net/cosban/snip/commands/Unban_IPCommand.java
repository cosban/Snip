package net.cosban.snip.commands;

import net.cosban.snip.api.SnipAPI;
import net.cosban.utils.commands.CommandBase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Unban_IPCommand extends SnipCommand {

	@CommandBase(
			name = "unban-ip",
			params = {},
			description = "Temporarily bans a player from bungee",
			aliases = { "unbanip", "ipunban", "ip-unban" },
			permission = "snip.unbanip")
	public Unban_IPCommand(String name) {
		super(name);
	}

	public Unban_IPCommand(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
	}

	public void execute(CommandSender sender, String[] args) {
		if (sender.hasPermission("snip.unbanip") || !(sender instanceof ProxiedPlayer)) {
			if (args.length == 1) {
				if (SnipAPI.isbanned(args[0])) {
					SnipAPI.unban(args[0], sender);
					sender.sendMessage(new TextComponent(ChatColor.GREEN + args[0] + " has been unbanned."));
				} else {
					sender.sendMessage(new TextComponent(ChatColor.RED + args[0] + " is not banned!"));
				}
			} else {
				sender.sendMessage(new TextComponent(ChatColor.RED + "Syntax: /unban-ip <address[/prefixlen]>"));
			}
		} else {
			sender.sendMessage(new TextComponent(ChatColor.RED + "You do not have permission for this command!"));
			SnipAPI.kickPlayer((ProxiedPlayer) sender, ChatColor.DARK_RED
					+ "YOU DO NOT HAVE PERMISSION FOR THIS COMMAND!", sender);
			return;
		}
	}

}
