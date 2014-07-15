package net.cosban.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeUtils {
	public static String getTime() {
		long time = System.currentTimeMillis();
		time /= 1000L;
		String sec = String.valueOf(time % 60L);
		if (time % 60L < 10L) sec = "0" + sec;
		String min = String.valueOf(time / 60L % 60L);
		if (time / 60L % 60L < 10L) min = "0" + min;

		String hr = checkValidTime(time / 60L / 60L % 24L);
		return hr + ":" + min + ":" + sec;
	}

	private static String checkValidTime(long i) {
		if (i > 23L) return checkValidTime(i - 24L);
		if (i < 0L) return checkValidTime(i + 24L);
		if (i < 10L) return String.valueOf("0" + i);
		return String.valueOf(i);
	}

	public static long timeToMS(String s) {
		String[] time = s.split("(?i)[smhd]");
		String[] units = new String[s.split("[0-9]+").length - 1];
		for (int i = 1; i < s.split("[0-9]+").length; i++) {
			units[i - 1] = s.split("[0-9]+")[i];
		}
		long total = 0L;
		for (int i = 0; i < units.length; i++) {
			switch (units[i]) {
				case "s":
				case "S":
					total += doSecondsToMS(Integer.valueOf(time[i]));
					break;
				case "m":
				case "M":
					total += doMinutesToMS(Integer.valueOf(time[i]));
					break;
				case "h":
				case "H":
					total += doHoursToMS(Integer.valueOf(time[i]));
					break;
				case "d":
				case "D":
					total += doDaysToMS(Integer.valueOf(time[i]));
					break;
				default:
					return -1L;
			}
		}
		return total;
	}

	private static long doSecondsToMS(int i) {
		return TimeUnit.SECONDS.toMillis(i);
	}

	private static long doMinutesToMS(int i) {
		return TimeUnit.MINUTES.toMillis(i);
	}

	private static long doHoursToMS(int i) {
		return TimeUnit.HOURS.toMillis(i);
	}

	private static long doDaysToMS(int i) {
		return TimeUnit.DAYS.toMillis(i);
	}

	public static String getDurationBreakdown(long seconds) {
		if (seconds <= 0L) {
			throw new IllegalArgumentException("Duration must be greater than zero!");
		}

		long days = TimeUnit.SECONDS.toDays(seconds);

		seconds -= TimeUnit.DAYS.toSeconds(days);

		long hours = TimeUnit.SECONDS.toHours(seconds);

		seconds -= TimeUnit.HOURS.toSeconds(hours);

		long minutes = TimeUnit.SECONDS.toMinutes(seconds);

		seconds -= TimeUnit.MINUTES.toSeconds(minutes);

		long secondz = TimeUnit.SECONDS.toSeconds(seconds);

		StringBuilder sb = new StringBuilder(64);
		sb.append(days);
		sb.append(" day(s) ");
		sb.append(hours);
		sb.append(" hour(s) ");
		sb.append(minutes);
		sb.append(" minute(s) ");
		sb.append(secondz);
		sb.append(" second(s)");

		return sb.toString();
	}

	public static String millisToDate(long milliseconds) {
		return new SimpleDateFormat("HH:mm dd-MM-yyyy").format(new Date(milliseconds));
	}

}
