package net.cosban.snip.events;

public class KickEvent extends SnipEvent {
	private String player;
	private String creator;
	private String reason;

	public KickEvent(String player, String creator, String reason) {
		this.player = player;
		this.creator = creator;
		this.reason = reason;
	}

	public String getPlayer() {
		return player;
	}

	public String getReason() {
		return reason;
	}

	public String getInvoker() {
		return creator;
	}
}
