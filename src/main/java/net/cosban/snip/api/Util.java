package net.cosban.snip.api;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Logger;

/**
 * LogBlock utils refactored for Forbiddance use.
 * 
 * @author DiddiZ
 */
public class Util {
	public static String	newline	= System.getProperty("line.separator");

	public static void download(Logger log, URL url, File file) throws IOException {
		if (!file.getParentFile().exists()) file.getParentFile().mkdir();
		if (file.exists()) file.delete();
		file.createNewFile();
		final int size = url.openConnection().getContentLength();
		log.info("Downloading " + file.getName() + " (" + size / 1024 + "kb) ...");
		final InputStream in = url.openStream();
		final OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		final byte[] buffer = new byte[1024];
		int len, downloaded = 0, msgs = 0;
		final long start = System.currentTimeMillis();
		while ((len = in.read(buffer)) >= 0) {
			out.write(buffer, 0, len);
			downloaded += len;
			if ((int) ((System.currentTimeMillis() - start) / 500) > msgs) {
				log.info((int) (downloaded / (double) size * 100d) + "%");
				msgs++;
			}
		}
		in.close();
		out.close();
		log.info("Download finished");
	}

	public static String readURL(URL url) throws IOException {
		final StringBuilder content = new StringBuilder();
		final BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null)
			content.append(inputLine);
		in.close();
		return content.toString();
	}

	public static boolean isInt(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (final NumberFormatException ex) {}
		return false;
	}

	public static boolean isByte(String str) {
		try {
			Byte.parseByte(str);
			return true;
		} catch (final NumberFormatException ex) {}
		return false;
	}

	public static String listing(String[] entries, String delimiter, String finalDelimiter) {
		final int len = entries.length;
		if (len == 0) return "";
		if (len == 1) return entries[0];
		final StringBuilder builder = new StringBuilder(entries[0]);
		for (int i = 1; i < len - 1; i++)
			builder.append(delimiter + entries[i]);
		builder.append(finalDelimiter + entries[len - 1]);
		return builder.toString();
	}

	public static String listing(List<?> entries, String delimiter, String finalDelimiter) {
		final int len = entries.size();
		if (len == 0) return "";
		if (len == 1) return entries.get(0).toString();
		final StringBuilder builder = new StringBuilder(entries.get(0).toString());
		for (int i = 1; i < len - 1; i++)
			builder.append(delimiter + entries.get(i).toString());
		builder.append(finalDelimiter + entries.get(len - 1).toString());
		return builder.toString();
	}

	public static int parseTimeSpec(String spec) {
		if (spec == null) {
			return -1;
		}

		if (isInt(spec)) {
			return Integer.valueOf(spec);
		}

		if (!spec.contains(":") && !spec.contains(".")) {
			int days = 0, hours = 0, minutes = 0;
			int lastIndex = 0, currIndex = 1;
			while (currIndex <= spec.length()) {
				while (currIndex <= spec.length() && isInt(spec.substring(lastIndex, currIndex))) {
					currIndex++;
				}
				if (currIndex - 1 != lastIndex) {
					final String param = spec.substring(currIndex - 1, currIndex).toLowerCase();
					if (param.equals("d")) {
						days = Integer.parseInt(spec.substring(lastIndex, currIndex - 1));
					}

					else if (param.equals("h")) {
						hours = Integer.parseInt(spec.substring(lastIndex, currIndex - 1));
					}

					else if (param.equals("m")) {
						minutes = Integer.parseInt(spec.substring(lastIndex, currIndex - 1));
					}
				}
				lastIndex = currIndex;
				currIndex++;
			}

			if (days == 0 && hours == 0 && minutes == 0) {
				return -1;
			}
			return minutes + hours * 60 + days * 1440;
		}

		final String timestamp;
		if (spec.contains(":")) {
			timestamp = new SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis()) + " " + spec;
		}

		else {
			timestamp = spec + " 00:00:00";
		}

		try {
			return (int) ((System.currentTimeMillis() - new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse(timestamp).getTime()) / 60000);
		} catch (final ParseException ex) {
			return -1;
		}
	}

	public static String spaces(int count) {
		final StringBuilder filled = new StringBuilder(count);
		for (int i = 0; i < count; i++)
			filled.append(' ');
		return filled.toString();
	}

	public static String join(String[] s, String delimiter) {
		if (s == null || s.length == 0) return "";
		final int len = s.length;
		final StringBuffer buffer = new StringBuffer(s[0]);
		for (int i = 1; i < len; i++)
			buffer.append(delimiter).append(s[i]);
		return buffer.toString();
	}
}
