package net.cosban.snip.events;

import net.cosban.snip.api.Ban.BanType;
import net.md_5.bungee.api.CommandSender;

public class BanEvent extends SnipEvent {
	private String			name;
	private CommandSender	invoker;
	private String			reason;
	private long			timestamp;
	private long			bantime;
	private BanType			type;

	public BanEvent(String name, CommandSender invoker, String reason, long timestamp, BanType type) {
		this.name = name;
		this.invoker = invoker;
		this.reason = reason;
		this.timestamp = timestamp;
		this.type = type;
	}

	public BanEvent(String name, CommandSender invoker, String reason, long timestamp, BanType type, long bantime) {
		this.name = name;
		this.invoker = invoker;
		this.reason = reason;
		this.timestamp = timestamp;
		this.bantime = bantime;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public CommandSender getInvoker() {
		return invoker;
	}

	public String getReason() {
		return reason;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public long getBanTime() {
		return bantime;
	}

	public BanType getType() {
		return type;
	}
}
