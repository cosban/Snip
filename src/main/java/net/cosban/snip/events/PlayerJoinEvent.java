package net.cosban.snip.events;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerJoinEvent extends SnipEvent {
	private Result        result;
	private ProxiedPlayer player;

	public PlayerJoinEvent(ProxiedPlayer player, Result result) {
		this.player = player;
		this.result = result;
	}

	public ProxiedPlayer getPlayer() {
		return player;
	}

	public Result getResult() {
		return result;
	}

	public enum Result {
		ALLOWED,
		BANNED,
		TEMPBANNED,
		FULL,
		WHITELIST,
		OTHER
	}
}
