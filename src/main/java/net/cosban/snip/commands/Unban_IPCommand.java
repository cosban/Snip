package net.cosban.snip.commands;

import net.cosban.snip.api.SnipAPI;
import net.cosban.utils.commands.CommandBase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Unban_IPCommand extends SnipCommand {

	@CommandBase(
			name = "unban-ip",
			params = { },
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
		if (args.length < 1) {
			sender.sendMessage(new TextComponent(ChatColor.RED + getSyntax()));
			return;
		}
		try {
			InetAddress address = InetAddress.getByName(args[0]);
			SnipAPI.unban(address, sender.getName());
			sender.sendMessage(new TextComponent(ChatColor.GREEN + args[0] + " has been unbanned."));
		} catch (UnknownHostException e) {
			sender.sendMessage(new TextComponent(ChatColor.DARK_RED + "Not a valid IP address!"));
		}
	}

}
