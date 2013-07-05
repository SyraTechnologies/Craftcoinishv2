package me.meta1203.plugins.craftcoin.commands;

import me.meta1203.plugins.craftcoin.Util;
import static me.meta1203.plugins.craftcoin.commands.CommandUtil.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.litecoin.core.Address;


public class DepositCommand implements CommandExecutor {

	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!arg0.hasPermission("craftcoin.deposit")) {
			error("You do not have permission for this command!", arg0);
			return true;
		}
		
		if (arg0 instanceof Player) {
			Player player = (Player)arg0;
			String name = player.getName();
			Address alloc = Util.parseAddress(Util.loadAccount(name).getAddr());
			info("Send Bitcoin to the following address: ", arg0);
			info("http://" + alloc.toString() + ".crc", arg0);
			info("This address is yours forever. \nAdd it to your address book if need-be.", arg0);
		}
		return true;
	}

}
