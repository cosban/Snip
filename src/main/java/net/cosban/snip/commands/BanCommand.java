package net.cosban.snip.commands;

import net.cosban.snip.api.SnipAPI;
import net.cosban.utils.commands.CommandBase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BanCommand extends SnipCommand {

	@CommandBase(
			name = "ban",
			params = { },
			description = "Bans a player from bungee",
			aliases = { },
			permission = "snip.ban")
	public BanCommand(String name) {
		super(name);
	}

	public BanCommand(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
	}

	public void execute(CommandSender sender, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(new TextComponent(ChatColor.RED + getSyntax()));
			return;
		}
		if (!args[0].matches("[_A-Za-z0-9]{1,16}")) {
			sender.sendMessage(new TextComponent(new TextComponent(ChatColor.RED
					+ "Invalid player name specified (1-16 chars of 'A-Za-z0-9_')")));
			return;
		}
		if (SnipAPI.isBanned(args[0])) {
			sender.sendMessage(new TextComponent(ChatColor.RED + args[0] + " is already banned!"));
			return;
		}
		ProxiedPlayer player;
		if ((player = ProxyServer.getInstance().getPlayer(args[0])) != null) {
			SnipAPI.ban(player.getName(), player.getUniqueId().toString(), player.getAddress().getAddress()
							.getHostAddress(),
					"Banned: "
							+ getMessage(args, 1), sender.getName());
			SnipAPI.kickPlayer(player.getName(), "Banned: " + getMessage(args, 1), sender.getName());
			sender.sendMessage(new TextComponent(ChatColor.GREEN + player.getName() + " has been banned."));
		} else if (!SnipAPI.isBanned(args[0].toLowerCase())) {
			SnipAPI.ban(args[0], "Banned: " + getMessage(args, 1), sender.getName());
			sender.sendMessage(new TextComponent(ChatColor.GREEN + args[0] + " has been banned."));
		}
	}
}
