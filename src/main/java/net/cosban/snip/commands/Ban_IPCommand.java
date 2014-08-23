package net.cosban.snip.commands;

import net.cosban.snip.Snip;
import net.cosban.snip.api.SnipAPI;
import net.cosban.utils.commands.CommandBase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Ban_IPCommand extends SnipCommand {

	@CommandBase(
			name = "banip",
			params = { "address" },
			description = "Bans a specified, or players IP or range",
			aliases = { "banip", "ipban", "ip-ban" },
			permission = "snip.banip")
	public Ban_IPCommand(String name) {
		super(name);
	}

	public Ban_IPCommand(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
	}

	public void execute(CommandSender sender, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(new TextComponent(ChatColor.RED + getSyntax()));
			return;
		}
		if (args[0].equals("-p")) {
			ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[1]);
			if (player != null) {
				SnipAPI.banIP(player.getName(),player.getUniqueId(),player.getAddress().getAddress(),
						"IP BANNED: " + getMessage(args,2),sender.getName());
				for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
					if (p.getAddress().getAddress().equals(player.getAddress().getAddress())) {
						SnipAPI.kickPlayer(p, "IP BANNED: " + getMessage(args, 2), sender.getName());
					}
				}
			} else {
				sender.sendMessage(new TextComponent(ChatColor.RED + args[1] + " is not online!"));
			}
		} else {
			try {
				SnipAPI.banIP(InetAddress.getByName(args[0]), "IP BANNED: " + getMessage(args, 1), sender.getName());
			} catch (UnknownHostException e) {
				Snip.debug().debug(getClass(), e);
			}
		}
	}
}
