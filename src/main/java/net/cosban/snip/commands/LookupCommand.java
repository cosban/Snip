package net.cosban.snip.commands;

import net.cosban.snip.api.SnipAPI;
import net.cosban.utils.TimeUtils;
import net.cosban.utils.commands.CommandBase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class LookupCommand extends SnipCommand {
	@CommandBase(
			name = "lookup",
			params = { },
			description = "Looks up whether a player or IP is banned",
			aliases = { "isbanned" },
			permission = "snip.lookup")
	public LookupCommand(String name) {
		super(name);
	}

	public LookupCommand(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
	}

	public void execute(CommandSender sender, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(new TextComponent(getSyntax()));
			return;
		}
		TextComponent t = new TextComponent();
		if (!SnipAPI.isBanned(args[0])) {
			sender.sendMessage(new TextComponent(ChatColor.GREEN + args[0] + " IS NOT BANNED!"));
		} else {
			if (SnipAPI.getLastBan(args[0]).isTemporary()) {
				sender.sendMessage(new TextComponent(ChatColor.GREEN + args[0] + " IS TEMPORARILY BANNED FOR " +
						TimeUtils.getDurationBreakdown(SnipAPI.getRemainingBanTime(args[0]))));
			} else {
				sender.sendMessage(new TextComponent(ChatColor.GREEN + args[0] + " IS BANNED"));
			}
		}

	}
}
