package net.cosban.snip.listeners;

import net.cosban.snip.api.Ban;
import net.cosban.snip.api.Ban.BanType;
import net.cosban.snip.api.SnipAPI;
import net.cosban.snip.events.BanEvent;
import net.cosban.snip.events.KickEvent;
import net.cosban.snip.events.PlayerJoinEvent;
import net.cosban.utils.TimeUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class SnipListener implements Listener {
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
						+ TimeUtils.getDurationBreakdown(SnipAPI.getLatestBan(event.getPlayer().getName(),
						event.getPlayer().getUniqueId(), event.getPlayer().getAddress().getAddress())
						.getRemainingBanTime())));
				break;
			case BANNED:
				Ban latest = SnipAPI.getLatestBan(event.getPlayer().getName(), event.getPlayer().getUniqueId(),
						event.getPlayer().getAddress().getAddress());
				event.getPlayer().disconnect(new TextComponent(latest.getReason().isEmpty() ? "BANNED BY: "
						+ latest.getCreator() : latest.getReason()));
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

	@EventHandler public void onBan(BanEvent event) {
		ProxyServer.getInstance().broadcast(new TextComponent(ChatColor.YELLOW
				+ event.getInvoker().getName()
				+ (event.getType().equals(BanType.PERMANENT) ? " permanently" : " temporarily")
				+ " banned "
				+ event.getName()
				+ " - "
				+ event.getReason()));
	}

	@EventHandler public void onKick(KickEvent event) {
		ProxyServer.getInstance().broadcast(new TextComponent(ChatColor.YELLOW
				+ event.getInvoker()
				+ " kicked "
				+ event.getPlayer()
				+ " - "
				+ event.getReason()));
	}
}
