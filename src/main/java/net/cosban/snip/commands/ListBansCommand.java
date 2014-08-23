package net.cosban.snip.commands;

import net.cosban.utils.commands.CommandBase;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class ListBansCommand extends SnipCommand {

	@CommandBase(
			name = "listbans",
			params = { },
			description = "Lists all active bans",
			aliases = { },
			permission = "snip.listbans")
	public ListBansCommand(String name) {
		super(name);
	}

	public ListBansCommand(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
	}

	public void execute(CommandSender sender, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(new TextComponent(getSyntax()));
			return;
		} else {
			sender.sendMessage(new TextComponent("You don't need this command, you ungrateful shit."));
			return;
		}
	}
}
