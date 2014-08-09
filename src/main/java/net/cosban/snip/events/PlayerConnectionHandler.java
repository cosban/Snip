package net.cosban.snip.events;

import java.net.InetAddress;

import net.cosban.snip.api.Ban;
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
		// TODO: if isn't connected to sql, revert to CSV
		if (SnipAPI.isbanned(event.getPlayer())) {
			if (!SnipAPI.isTemporary(event.getPlayer())) {
				event.getPlayer().disconnect(new TextComponent(
						SnipAPI.getBanReason(event.getPlayer()).isEmpty() ? "Banned by: "
								+ SnipAPI.getCreator(event.getPlayer()) : "Banned: "
								+ SnipAPI.getBanReason(event.getPlayer())));
			} else {
				event.getPlayer().disconnect(new TextComponent(
						SnipAPI.getBanReason(event.getPlayer()).isEmpty() ? "Banned! R: "
								+ TimeUtils.getDurationBreakdown(SnipAPI.getRemainingBanTime(event.getPlayer())) : "Temp Banned: "
								+ SnipAPI.getBanReason(event.getPlayer())
								+ " R: "
								+ TimeUtils.getDurationBreakdown(SnipAPI.getRemainingBanTime(event.getPlayer()))));
			}
		} 
		// else if
		// (isAddressBanned(event.getPlayer().getAddress().getAddress())) {
		// event.getPlayer().disconnect(new TextComponent("Banned!"));
		// }
	}

	public boolean isAddressBanned(InetAddress address) {
		// TODO : it'd be cool if we could ban all of Italy again
		for (Ban key : SnipAPI.bannedIPs(address.getHostAddress())) {
			if (key.isBanned()) return true;
		}
		return false;
	}
}
