package net.cosban.snip.events;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class KickEvent extends ForbiddanceEvent {
	private ProxiedPlayer	player;
	private CommandSender	invoker;
	private String			reason;

	public KickEvent(ProxiedPlayer player, CommandSender invoker, String reason) {
		this.player = player;
		this.invoker = invoker;
		this.reason = reason;
	}

	public ProxiedPlayer getPlayer() {
		return player;
	}

	public String getReason() {
		return reason;
	}

	public CommandSender getInvoker() {
		return invoker;
	}
}
