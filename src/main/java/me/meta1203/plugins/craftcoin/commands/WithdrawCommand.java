package me.meta1203.plugins.craftcoin.commands;

import me.meta1203.plugins.craftcoin.Craftcoinish;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.litecoin.core.Address;
import com.google.litecoin.core.AddressFormatException;
import com.google.litecoin.core.WrongNetworkException;

import static me.meta1203.plugins.craftcoin.commands.CommandUtil.*;

public class WithdrawCommand implements CommandExecutor {

	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!arg0.hasPermission("craftcoin.withdraw")) {
			error("You do not have permission for this command!", arg0);
			return true;
		}
		
		if (arg0 instanceof Player) {
			Player player = (Player)arg0;
			
			// Withdraw exact amount
			if (arg3.length == 2) {
				try {
				
					Address withdrawTo = new Address(Craftcoinish.network, arg3[0]);
					double withdraw = Double.parseDouble(arg3[1]);
					if (!Craftcoinish.econ.hasMoney(player.getName(), Craftcoinish.minWithdraw)) {
						error("Oops! You must have " + Craftcoinish.econ.formatValue(Craftcoinish.minWithdraw, true) + " to withdraw!", arg0);
						return true;
					}
					
					if (!Craftcoinish.econ.hasMoney(arg0.getName(), withdraw - Craftcoinish.econ.priceOfTax(withdraw) - 0.2)) {
						error("Oops! You cannot withdraw more money than you have!", arg0);
						return true;
					}
					Craftcoinish.bapi.localSendCoins(withdrawTo, withdraw);


					
					Craftcoinish.econ.subFunds(arg0.getName(), withdraw - 0.2);
				} catch (WrongNetworkException e) {
					error("Oops! That address was for the TestNet!", arg0);
				} catch (AddressFormatException e) {
					error("Oops! Is that the correct address?", arg0);
				} catch (NumberFormatException e) {
					error("Syntax: /withdraw <address> [amount]", arg0);
					error("Amount must be a number!",arg0);
				}
			} 
		}
		
		return true;
	}
}
