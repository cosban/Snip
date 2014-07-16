package net.cosban.snip.commands;

import net.cosban.snip.Snip;
import net.cosban.snip.api.SnipAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class UnbanCommand extends SnipCommand {

	public UnbanCommand(Snip instance, String name, String permission, String[] aliases) {
		super(instance, name, permission, aliases);
	}

	public void execute(CommandSender sender, String[] args) {
		if (sender.hasPermission("snip.unban") || !(sender instanceof ProxiedPlayer)) {
			if (args.length == 1) {
				if (!args[0].matches("[_A-Za-z0-9]{1,16}")) {
					sender.sendMessage(new TextComponent(ChatColor.RED
							+ "Invalid player name specified (1-16 chars of \'A-Za-z0-9_\')"));
				}

				if (SnipAPI.isbanned(args[0])) {
					SnipAPI.unban(args[0], sender);
					sender.sendMessage(new TextComponent(ChatColor.GREEN + args[0] + " has been unbanned."));
				} else {
					sender.sendMessage(new TextComponent(ChatColor.RED + args[0] + " is not banned!"));
				}
			} else {
				sender.sendMessage(new TextComponent(ChatColor.RED + "Syntax: /unban <player>"));
			}
		} else {
			sender.sendMessage(new TextComponent(ChatColor.RED + "You do not have permission for this command!"));
			SnipAPI.kickPlayer((ProxiedPlayer) sender, ChatColor.DARK_RED
					+ "YOU DO NOT HAVE PERMISSION FOR THIS COMMAND!", sender);
		}
	}
}
