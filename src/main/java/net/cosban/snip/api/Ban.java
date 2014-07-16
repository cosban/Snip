package net.cosban.snip.api;

import java.net.InetAddress;

public class Ban {
	private String		player;
	private String		uuid;
	private InetAddress	address;
	private String		reason;
	private BanType		type;
	private String		creator;
	private boolean		temp;
	private long		bantime;
	private long		remainingbantime;
	private long		creationtime;
	private boolean		banned;

	public Ban(String player, String uuid, InetAddress address, String reason, BanType type, String creator,
			boolean temporary, long bantime, long creationtime, boolean banned) {
		this.player = player;
		this.uuid = uuid;
		this.address = address;
		this.reason = reason;
		this.type = type;
		this.creator = creator;
		this.temp = temporary;
		this.bantime = bantime;
		this.creationtime = creationtime;
	}

	public Ban(InetAddress address, String reason, String creator, boolean banned) {
		this.player = "";
		this.uuid = "";
		this.address = address;
		this.reason = reason;
		this.type = BanType.IPV4;
		this.creator = creator;
		this.temp = false;
		this.bantime = -1L;
		this.creationtime = 0L;
	}

	public String getPlayerName() {
		return player.toLowerCase();
	}

	public String getUUID() {
		return uuid;
	}

	public InetAddress getAddress() {
		return address;
	}

	public String getReason() {
		return reason;
	}

	public BanType getType() {
		return type;
	}

	public String getCreator() {
		return creator.toLowerCase();
	}

	public long getBanCreationTime() {
		return creationtime;
	}

	public long getBanTime() {
		return bantime;
	}

	public long getRemainingBanTime() {
		return remainingbantime;
	}

	public boolean isTemporary() {
		return temp;
	}

	public boolean isBanned() {
		return banned;
	}

	@Override
	public String toString() {
		return toString();
	}

	public enum BanType {
		PERMANENT("PERMANENT"),
		HELLBAN("HELLBAN"),
		TEMPORARY("TEMPORARY"),
		IPV4("IPV4"),
		IPV6("IPV6"),
		IPV4_CIDR("IPV4_CIDR"),
		IPV6_CIDR("IPV6_CIDR"),
		UNBAN("UNBAN");

		private String	toString;

		private BanType(String name) {
			this.toString = name;
		}

		@Override
		public String toString() {
			return toString;
		}
	}
}