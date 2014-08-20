package net.cosban.snip.events;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerDisconnectEvent {
	private Reason        reason;
	private ProxiedPlayer player;

	public PlayerDisconnectEvent(ProxiedPlayer player, Reason result) {
		this.player = player;
		this.reason = result;
	}

	public ProxiedPlayer getPlayer() {
		return player;
	}

	public Reason getResult() {
		return reason;
	}

	public enum Reason {
		QUIT,
		KICKED,
		BANNED,
		OTHER
	}
}
