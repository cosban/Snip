package net.cosban.snip.commands;

import net.cosban.snip.Snip;
import net.cosban.snip.api.SnipAPI;
import net.cosban.utils.commands.CommandBase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class UnbanCommand extends SnipCommand {

	@CommandBase(
			name = "unban",
			params = { },
			description = "Unbans a player from bungee",
			aliases = { },
			permission = "snip.unban")
	public UnbanCommand(String name) {
		super(name);
	}

	public UnbanCommand(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
	}

	public void execute(CommandSender sender, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(new TextComponent(ChatColor.RED + getSyntax()));
			return;
		}
		if (!args[0].matches("[_A-Za-z0-9]{1,16}")) {
			sender.sendMessage(new TextComponent(ChatColor.RED
					+ "Invalid player name specified (1-16 chars of \'A-Za-z0-9_\')"));
		}

		if (SnipAPI.isBanned(args[0])) {
			SnipAPI.unban(args[0], sender.getName());
			sender.sendMessage(new TextComponent(ChatColor.GREEN + args[0] + " has been unbanned."));
		} else {
			sender.sendMessage(new TextComponent(ChatColor.RED + args[0] + " is not banned!"));
		}
	}
}
