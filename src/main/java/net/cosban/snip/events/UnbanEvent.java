package net.cosban.snip.events;

import net.md_5.bungee.api.CommandSender;

public class UnbanEvent extends SnipEvent {
	private String			name;
	private CommandSender	invoker;

	public UnbanEvent(String name, CommandSender invoker) {
		this.name = name;
		this.invoker = invoker;
	}

	public String getName() {
		return name;
	}

	public CommandSender getInvoker() {
		return invoker;
	}
}
