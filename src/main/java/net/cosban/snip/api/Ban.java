package net.cosban.snip.api;

import net.cosban.snip.Snip;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Ban {
	private String      player;
	private String      uuid;
	private InetAddress address;
	private String      reason;
	private BanType     type;
	private String      creator;
	private long        duration;
	private long        creationtime;
	private boolean     banned;

	public Ban(String player, String uuid, String address, String reason, BanType type, String creator, long duration,
			long creationtime, boolean banned) {
		this.player = player;
		this.uuid = uuid;
		//TODO: check for this before construction
		try {
			this.address = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			Snip.debug().debug(getClass(), e);
			this.address = null;
		}
		this.reason = reason;
		this.type = type;
		this.creator = creator;
		this.duration = duration;
		this.banned = banned;
		this.creationtime = creationtime;
	}

	public Ban(String address, String reason, String creator, long creationtime, boolean banned) {
		this.player = "";
		this.uuid = "";
		//TODO: check for this before construction
		try {
			this.address = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			Snip.debug().debug(getClass(), e);
			this.address = null;
		}
		this.reason = reason;
		this.type = BanType.IPV4;
		this.creator = creator;
		this.duration = 0L;
		this.banned = banned;
		this.creationtime = creationtime;
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

	public long getCreationTime() {
		return creationtime;
	}

	public long getDuration() {
		return duration;
	}

	public long getRemainingBanTime() {
		return duration - (System.currentTimeMillis() - getCreationTime());
	}

	public boolean isTemporary() {
		return type.equals(BanType.TEMPORARY);
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
		ALTBAN("ALTBAN"),
		TEMPORARY("TEMPORARY"),
		IPV4("IPV4"),
		IPV6("IPV6"),
		IPV4_CIDR("IPV4_CIDR"),
		IPV6_CIDR("IPV6_CIDR"),
		UNBAN("UNBAN");

		private String toString;

		private BanType(String name) {
			this.toString = name;
		}

		@Override
		public String toString() {
			return toString;
		}
	}
}
