package net.cosban.snip.events;

public class UnbanEvent extends SnipEvent {
	private String name;
	private String creator;

	public UnbanEvent(String name, String creator) {
		this.name = name;
		this.creator = creator;
	}

	public String getName() {
		return name;
	}

	public String getInvoker() {
		return creator;
	}
}
