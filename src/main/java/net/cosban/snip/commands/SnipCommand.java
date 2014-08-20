package net.cosban.snip.commands;

import net.cosban.snip.Snip;
import net.cosban.snip.api.SnipAPI;
import net.cosban.utils.commands.CommandBase;
import net.cosban.utils.commands.ParameterBase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SnipCommand extends Command {
	public Map<String, Method> params = new HashMap<>();

	@CommandBase(
			name = "snip",
			params = { "status", "info", "size" },
			description = "Plugin administration and information",
			aliases = { "forbiddance" },
			permission = "snip.snip")
	public SnipCommand(String name) {
		super(name);
	}

	public SnipCommand(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
	}

	public void execute(CommandSender sender, String[] args) {
		if (sender.hasPermission("snip.snip") || !(sender instanceof ProxiedPlayer)) {
			if (args.length <= 0) {
				sender.sendMessage(new TextComponent(ChatColor.AQUA
						+ "Possible commands: connect, disconnect, status, info, size, import, save"));
				return;
			}

			if (args[0].equalsIgnoreCase("status")) {
				status(sender);
			} else if (args[0].equalsIgnoreCase("info")) {
				info(sender);
			} else if (args[0].equalsIgnoreCase("size")) {
				size(sender);
			} else if (args[0].equalsIgnoreCase("import")) {
				_import(sender);
			} else {
				sender.sendMessage(new TextComponent(ChatColor.RED + "Invalid Snip command!"));
				return;
			}
		} else {
			sender.sendMessage(new TextComponent(ChatColor.RED + "You do not have permission for this command!"));
			SnipAPI.kickPlayer((ProxiedPlayer) sender, ChatColor.DARK_RED
					+ "YOU DO NOT HAVE PERMISSION FOR THIS COMMAND!", sender);
			return;
		}
		return;
	}

	public void status(CommandSender sender) {
		sender.sendMessage(new TextComponent(ChatColor.GREEN
				+ "Snip is currently using "
				+ (Snip.isConnected() ? "MySQL" : "a flat file")
				+ " to store bans."));
	}

	private void info(CommandSender sender) {
		sender.sendMessage(new TextComponent(ChatColor.GREEN + "Running Snip v" + Snip.getVersion()));
		return;
	}

	private void size(CommandSender sender) {
		// if (jedis.isConnected()) {
		// sender.sendMessage(new TextComponent(ChatColor.GREEN
		// + "Currently holding "
		// + jedis.dbSize().toString()
		// + " keys."));
		// return;
		// } else if (!jedis.isConnected()) {
		// sender.sendMessage(new TextComponent(ChatColor.RED +
		// "Database is not connected!"));
		return;
	}

	private void _import(CommandSender sender) {
		/*
		 * if (jedis.isConnected()) {
		 * BanImport banimport = new BanImport(new
		 * File(Bukkit.getServer(
		 * ).getPluginManager().getPlugin("Snip"
		 * ).getDataFolder().getPath() + "/bans.csv"));
		 * banimport.setName("BanImport");
		 * banimport.setPriority(Thread.MIN_PRIORITY);
		 * banimport.start();
		 * sender.sendMessage(new TextComponent(ChatColor.BLUE +
		 * "Started import!");
		 * }
		 */
		sender.sendMessage(new TextComponent(ChatColor.RED + "This command has been disabled."));
	}

	public boolean handlePerms(CommandSender sender, Command command) {
		if (!checkPerms(sender, command)) {
			SnipAPI.kickPlayer(sender.getName(), ChatColor.RED
					+ "You do not have permission for this command!", sender);
		}
		return checkPerms(sender, command);
	}

	public boolean handlePerms(CommandSender sender, String name) {
		return handlePerms(sender, this);
	}

	public boolean checkPerms(CommandSender sender, Command command) {
		return sender.hasPermission(command.getPermission());
	}

	public void registerParams() {
		for (Method method : getClass().getMethods())
			if (method.isAnnotationPresent(ParameterBase.class)) {
				ParameterBase annote = (ParameterBase) method.getAnnotation(ParameterBase.class);
				if (getParams().get(annote.name()) == null) getParams().put(annote.name(), method);
			}
	}

	public Map<String, Method> getParams() {
		return params;
	}

	public ParameterBase getParameterBase(String s) {
		return (ParameterBase) ((Method) getParams().get(s)).getAnnotation(ParameterBase.class);
	}

	public List<Character> getFlags(String[] args) {
		List<Character> ch = new ArrayList<>();
		for (String s : args) {
			if (s.startsWith("-")) {
				for (char c : s.toCharArray()) {
					if (!ch.contains(c)) ch.add(c);
				}
			}
		}
		return ch;
	}

	public List<Character> getFlags(String args) {
		List<Character> ch = new ArrayList<>();
		if (args.startsWith("-")) {
			for (char c : args.toCharArray()) {
				if (!ch.contains(c)) ch.add(c);
			}
			return ch;
		}
		return null;
	}

	public int getInt(String[] args) {
		Pattern reg = Pattern.compile("^[0-9]+$");
		for (String s : args)
			if (reg.matcher(s).matches()) return Integer.parseInt(s);
		return -1;
	}

	public String getSyntax() {
		String p = "";
		for (String s : getParams().keySet()) {
			p += s + "|";
		}
		if (p.contains("|")) {
			p = p.substring(0, p.lastIndexOf("|"));
		}
		return "/" + getName() + " <" + p + ">";
	}
}
