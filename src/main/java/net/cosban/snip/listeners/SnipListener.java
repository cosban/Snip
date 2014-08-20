package net.cosban.snip.listeners;

import net.cosban.snip.api.Ban.BanType;
import net.cosban.snip.events.BanEvent;
import net.cosban.snip.events.KickEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class SnipListener implements Listener {
	@EventHandler
	public void onBan(BanEvent event) {
		ProxyServer.getInstance().broadcast(new TextComponent(ChatColor.YELLOW
				+ event.getInvoker().getName()
				+ (event.getType().equals(BanType.PERMANENT) ? " permanently" : " temporarily")
				+ " banned "
				+ event.getName()
				+ " - "
				+ event.getReason()));
	}

	@EventHandler
	public void onKick(KickEvent event) {
		ProxyServer.getInstance().broadcast(new TextComponent(ChatColor.YELLOW
				+ event.getInvoker().getName()
				+ " kicked "
				+ event.getPlayer().getName()
				+ " - "
				+ event.getReason()));
	}
}
