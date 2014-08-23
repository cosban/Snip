package net.cosban.snip.listeners;

import net.cosban.snip.api.SnipAPI;
import net.cosban.snip.events.PlayerJoinEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ConnectionListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PostLoginEvent event) {
		if (SnipAPI.isBanned(event.getPlayer().getUniqueId())) {
			if (SnipAPI.isTemporary(event.getPlayer().getUniqueId())
					|| SnipAPI.isTemporary(event.getPlayer().getName())) {
				ProxyServer.getInstance().getPluginManager().callEvent(new PlayerJoinEvent(event.getPlayer(),
						PlayerJoinEvent.Result.TEMPBANNED));
			} else {
				ProxyServer.getInstance().getPluginManager().callEvent(new PlayerJoinEvent(event.getPlayer(),
						PlayerJoinEvent.Result.BANNED));
			}
		} else if (SnipAPI.isBanned(event.getPlayer().getAddress().getAddress())) {
			ProxyServer.getInstance().getPluginManager().callEvent(new PlayerJoinEvent(event.getPlayer(),
					PlayerJoinEvent.Result.BANNED));
		} else {
			ProxyServer.getInstance().getPluginManager().callEvent(new PlayerJoinEvent(event.getPlayer(),
					PlayerJoinEvent.Result.ALLOWED));
		}
	}
}
