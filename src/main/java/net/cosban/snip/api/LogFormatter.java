package net.cosban.snip.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

	@Override
	public String format(LogRecord record) {
		return "["
				+ new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(record.getMillis()))
				+ "] "
				+ record.getMessage()
				+ "\n";
	}
}
