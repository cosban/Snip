package net.cosban.snip.commands;

import net.cosban.snip.Snip;
import net.cosban.snip.api.SnipAPI;
import net.cosban.utils.commands.CommandBase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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
			}
		} else {
			sender.sendMessage(new TextComponent(ChatColor.RED + "You do not have permission for this command!"));
			SnipAPI.kickPlayer(sender.getName(), ChatColor.DARK_RED
					+ "YOU DO NOT HAVE PERMISSION FOR THIS COMMAND!", sender.getName());
		}
	}

	public void status(CommandSender sender) {
		sender.sendMessage(new TextComponent(ChatColor.GREEN
				+ "Snip is currently using "
				+ (Snip.isConnected() ? "MySQL" : "a flat file")
				+ " to store bans."));
	}

	private void info(CommandSender sender) {
		sender.sendMessage(new TextComponent(ChatColor.GREEN + "Running Snip v" + Snip.getVersion()));

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

	public Map<String, Method> getParams() {
		return params;
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

	protected String getMessage(String[] args, int index) {
		String message = "";
		for (int i = index; i < args.length; i++) {
			message += args[i] + " ";
		}
		if(message.equals("")){
			message = "For breaking the rules!";
		}
		return message.trim();
	}
}
