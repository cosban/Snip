package net.cosban.snip.listeners;

import net.cosban.snip.api.Ban;
import net.cosban.snip.api.SnipAPI;
import net.cosban.snip.events.PlayerJoinEvent;
import net.cosban.utils.TimeUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.net.InetAddress;

public class ConnectionListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PostLoginEvent event) {
		if (SnipAPI.isbanned(event.getPlayer())) {
			if (SnipAPI.isTemporary(event.getPlayer())) {
				ProxyServer.getInstance().getPluginManager().callEvent(new PlayerJoinEvent(event.getPlayer(),
						PlayerJoinEvent.Result.TEMPBANNED));
			} else {
				ProxyServer.getInstance().getPluginManager().callEvent(new PlayerJoinEvent(event.getPlayer(),
						PlayerJoinEvent.Result.BANNED));
			}
		} else {
			ProxyServer.getInstance().getPluginManager().callEvent(new PlayerJoinEvent(event.getPlayer(),
					PlayerJoinEvent.Result.ALLOWED));
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		switch (event.getResult()) {
			case ALLOWED:
				event.getPlayer().getName();
				ProxyServer.getInstance().broadcast(new TextComponent(ChatColor.YELLOW
						+ event.getPlayer().getName()
						+ " has joined."));
				break;
			case TEMPBANNED:
				event.getPlayer().disconnect(new TextComponent("Banned until: "
						+ TimeUtils.getDurationBreakdown(SnipAPI.getRemainingBanTime(event.getPlayer()))));
				break;
			case BANNED:
				event.getPlayer().disconnect(new TextComponent(SnipAPI.getBanReason(event.getPlayer()).isEmpty() ?
						"Banned by: "
								+ SnipAPI.getCreator(event.getPlayer()) : "Reason: "
						+ SnipAPI.getBanReason(event.getPlayer())));
				break;
			case FULL:
				//TODO: Configureable
				event.getPlayer().disconnect(new TextComponent("Server is full, please try again soon!"));
				break;
			case WHITELIST:
			case OTHER:
			default:
				event.getPlayer().sendMessage(new TextComponent("Tell cosban that something is messed up with snip"));
		}
	}

	public boolean isAddressBanned(InetAddress address) {
		// TODO : it'd be cool if we could ban all of Italy again
		for (Ban key : SnipAPI.bannedIPs(address.getHostAddress())) {
			if (key.isBanned()) return true;
		}
		return false;
	}
}
