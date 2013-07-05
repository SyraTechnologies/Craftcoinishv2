package me.meta1203.plugins.craftcoin.commands;

import me.meta1203.plugins.craftcoin.Craftcoinish;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static me.meta1203.plugins.craftcoin.commands.CommandUtil.*;

public class CheckCommand implements CommandExecutor {

	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg0.hasPermission("craftcoin.info")) {
			arg0.sendMessage(Craftcoinish.scanner.getInfo());
		} else {
			error("You do not have permission for this command!", arg0);
		}
		return true;
	}

}
