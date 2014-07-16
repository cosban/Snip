package net.cosban.snip.events;

import java.net.InetAddress;

import org.apache.commons.net.util.SubnetUtils;

import net.cosban.snip.api.SnipAPI;
import net.cosban.utils.TimeUtils;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PlayerConnectionHandler implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PostLoginEvent event) {
		// TODO: if isn't connected to sql, revert to flatfile
		if (SnipAPI.isbanned(event.getPlayer().getName())) {
			if (!SnipAPI.isTemporary(event.getPlayer().getName())) {
				event.getPlayer().disconnect(new TextComponent(
						SnipAPI.getBanReason(event.getPlayer().getName()).isEmpty() ? "Banned!" : "Banned: "
								+ SnipAPI.getBanReason(event.getPlayer().getName())));
			} else {
				event.getPlayer().disconnect(new TextComponent(
						SnipAPI.getBanReason(event.getPlayer().getName()).isEmpty() ? "Banned! R: "
								+ TimeUtils.getDurationBreakdown(SnipAPI.getRemainingBanTime(event.getPlayer().getName())) : "Temp Banned: "
								+ SnipAPI.getBanReason(event.getPlayer().getName())
								+ " R: "
								+ TimeUtils.getDurationBreakdown(SnipAPI.getRemainingBanTime(event.getPlayer().getName()))));
			}
		} else if (isAddressBannedCIDR(event.getPlayer().getAddress().getAddress())) {
			event.getPlayer().disconnect(new TextComponent("Banned!"));
		} else if (SnipAPI.isbanned(event.getPlayer())) {
			event.getPlayer().disconnect(new TextComponent(
					SnipAPI.getBanReason(event.getPlayer().getAddress().getHostName()).isEmpty() ? "Banned!" : "Banned: "
							+ SnipAPI.getBanReason(event.getPlayer().getAddress().getHostName())));
		}
	}

	/*
	 * Disabled due to handling time taking too long.
	 * @EventHandler(priority = EventPriority.HIGHEST)
	 * public void onServerListPing(ServerListPingEvent event) {
	 * if (api.isConnected()) {
	 * if (api.isbanned(event.getAddress())) {
	 * if (!api.isTemporary(event.getAddress())) {
	 * event.setMotd("Banned: " + api.getBanReason(event.getAddress()));
	 * } else {
	 * event.setMotd("Temp banned. R: " +
	 * Forbiddance.getDurationBreakdown(api.getRemainingBanTime
	 * (event.getAddress())));
	 * }
	 * }
	 * }
	 * }
	 */

	public boolean isAddressBannedCIDR(InetAddress address) {
		for (String key : SnipAPI.keys("*/[0-9]*")) {
			if (new SubnetUtils(key).getInfo().isInRange(address.getHostAddress())) {
				return true;
			}
		}
		return false;
	}
}
