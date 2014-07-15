package net.cosban.snip.commands;

import net.cosban.snip.api.SnipAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class SnipCommand extends Command {

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
		// if (jedis.isConnected()) {
		// sender.sendMessage(new TextComponent(ChatColor.GREEN +
		// "Database is connected!"));
		// return;
		// } else if (!jedis.isConnected()) {
		// sender.sendMessage(new TextComponent(ChatColor.GREEN +
		// "Database is connected!"));
		return;
	}

	private void info(CommandSender sender) {
		// if (jedis.isConnected()) {
		// sender.sendMessage(new TextComponent(ChatColor.GREEN +
		// jedis.info()));
		// return;
		// } else if (!jedis.isConnected()) {
		// sender.sendMessage(new TextComponent(ChatColor.RED +
		// "Database not connected!"));
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
}
