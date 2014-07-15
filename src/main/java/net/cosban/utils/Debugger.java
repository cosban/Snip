package net.cosban.utils;

import java.util.logging.Logger;

public class Debugger {

	private boolean			enabled;
	private static Logger	log;

	public Debugger(String name, boolean enabled) {
		log = Logger.getLogger(name);
		this.enabled = enabled;
		if (enabled) debug(getClass(), "DEBUG IS ENABLED AND MAY CAUSE EXCESSIVE OUTPUT TO YOUR CONSOLE");
	}

	public void debug(Class<?> c, String s) {
		if (enabled) log.info("[" + c.getName() + "] - DEBUG - " + s);
	}

	public void debug(Class<?> c, Exception e) {
		if (enabled) {
			log.warning("["
					+ c.getName()
					+ "] Please report the following issue if it breaks functionality of the plugin");
			log.warning("[" + c.getName() + "] - DEBUGGING EXCEPTION BEGIN -");
			e.printStackTrace();
			log.warning("[" + c.getName() + "] - DEBUGGING EXCEPTION END-");
		}
	}
}
