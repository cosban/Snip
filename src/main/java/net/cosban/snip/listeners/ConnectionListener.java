package net.cosban.snip.listeners;

import net.cosban.snip.api.Ban;
import net.cosban.snip.api.SnipAPI;
import net.cosban.snip.events.BanEvent;
import net.cosban.snip.events.KickEvent;
import net.cosban.snip.events.PlayerJoinEvent;
import net.cosban.snip.events.PlayerLeaveEvent;
import net.cosban.utils.TimeUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.ArrayList;

public class ConnectionListener implements Listener {

	private ArrayList<ProxiedPlayer> rejects;

	public ConnectionListener() {
		rejects = new ArrayList<>();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PostLoginEvent event) {
		if (SnipAPI.isBanned(event.getPlayer().getUniqueId())) {
			rejects.add(event.getPlayer());
			if (SnipAPI.getLastBan(event.getPlayer().getUniqueId()).isTemporary()
					|| SnipAPI.getLastBan(event.getPlayer().getName()).isTemporary()) {
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
				rejects.add(event.getPlayer());
				event.getPlayer().disconnect(new TextComponent("Server is full, please try again soon!"));
				break;
			case WHITELIST:
			case OTHER:
			default:
				event.getPlayer().sendMessage(new TextComponent("Tell cosban that something is messed up with snip"));
		}
	}

	@EventHandler public void onPlayerQuit(PlayerDisconnectEvent event) {
		if (!rejects.contains(event.getPlayer())) {
			ProxyServer.getInstance().broadcast(new TextComponent(ChatColor.YELLOW
					+ event.getPlayer().getName()
					+ " has disconnected"));
			ProxyServer.getInstance().getPluginManager().callEvent(new PlayerLeaveEvent(event.getPlayer(),
					PlayerLeaveEvent.Reason.Unforced));
		} else {
			rejects.remove(event.getPlayer());
			ProxyServer.getInstance().getPluginManager().callEvent(new PlayerLeaveEvent(event.getPlayer(),
					PlayerLeaveEvent.Reason.Forced));
		}
	}

	@EventHandler public void onBan(BanEvent event) {
		rejects.add(ProxyServer.getInstance().getPlayer(event.getName()));
		ProxyServer.getInstance().broadcast(new TextComponent(ChatColor.YELLOW
				+ event.getInvoker().getName()
				+ (event.getType().equals(Ban.BanType.PERMANENT) ? " permanently" : " temporarily")
				+ " banned "
				+ event.getName()
				+ " - "
				+ event.getReason()));
	}

	@EventHandler public void onKick(KickEvent event) {
		rejects.add(ProxyServer.getInstance().getPlayer(event.getPlayer()));
		ProxyServer.getInstance().broadcast(new TextComponent(ChatColor.YELLOW
				+ event.getInvoker()
				+ " kicked "
				+ event.getPlayer()
				+ " - "
				+ event.getReason()));
	}
}
