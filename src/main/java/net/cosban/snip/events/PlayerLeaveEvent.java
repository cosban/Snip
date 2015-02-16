package net.cosban.snip.events;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerLeaveEvent extends SnipEvent {
	private ProxiedPlayer player;
	private Reason reason;

	public PlayerLeaveEvent(ProxiedPlayer player, Reason reason){
		this.player = player;
		this.reason = reason;
	}

	public ProxiedPlayer getPlayer(){
		return player;
	}

	public Reason getReason(){
		return reason;
	}

	public enum Reason {
		Forced,
		Unforced
	}
}
