package me.meta1203.plugins.craftcoin.commands;

import java.math.BigInteger;
import java.util.Set;

import me.meta1203.plugins.craftcoin.AuctionEntry;
import me.meta1203.plugins.craftcoin.Craftcoinish;
import static me.meta1203.plugins.craftcoin.commands.CommandUtil.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import com.lambdaworks.jni.Platform.OS;



public class AuctionsCommand implements CommandExecutor {
	public static Craftcoinish plugin;
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!arg0.hasPermission("craftcoin.admin")) {
			error("You do not have permission for this command!", arg0);
			return true;
		}
		if(arg3.length == 3)
		{
			if(arg3[0].equalsIgnoreCase("bid"))
			{
				AuctionEntry auc = Craftcoinish.auc.searchid( arg3[1] );
				if(Double.parseDouble(arg3[2]) > auc.getPrice() && Double.parseDouble(arg3[2]) <= Craftcoinish.econ.getMoney(arg0.getName()))
				{
					auc.setPrice(Double.parseDouble(arg3[2]));
					auc.setBidder(arg0.getName());
					if (plugin == null) {
						Plugin p = Bukkit.getPluginManager().getPlugin("Craftcoinish");
						plugin = (Craftcoinish) p;
						error("You have successfully bid " + arg3[2], arg0);
					}
					plugin.getDatabase().save(auc);
					Craftcoinish.econ.subFunds(auc.getOwner(),auc.getPrice() - Craftcoinish.econ.priceOfTax(auc.getPrice()));
				}
				else
				{
					error("You can not bid more money than you have.",arg0);
				}
			}
		}
		else
		{
		if(arg3.length != 2)
		{
			if(arg3.length != 3)
			{
			
				error("Syntax: /auction <search>|<create>|<buy>|<bid> <id>|<item>|<price> <amount>", arg0);
				return true;
			}
		}
		}
		if(arg3.length == 2)
		{
			if(arg3[0].equalsIgnoreCase("buy"))
			{
				AuctionEntry auc = Craftcoinish.auc.searchid( arg3[1] );
				if(auc.getBuyout() <= Craftcoinish.econ.getMoney(arg0.getName()))
				{
					
					if (plugin == null) {
						Plugin p = Bukkit.getPluginManager().getPlugin("Craftcoinish");
						plugin = (Craftcoinish) p;
					}
					Craftcoinish.econ.subFunds(arg0.getName(),auc.getBuyout() - Craftcoinish.econ.priceOfTax(auc.getPrice()));
					Bukkit.getPlayer(arg0.getName()).sendMessage(ChatColor.RED + "You have won the auction ");
					ItemStack sx = new ItemStack(Material.matchMaterial(auc.getItemname()), auc.getStack());
					Bukkit.getPlayer(arg0.getName()).getInventory().addItem(sx);
					Bukkit.getPlayer(arg0.getName()).updateInventory();
					plugin.getDatabase().delete(auc);
				}
				else
				{
					error("You can not buy more money than you have.",arg0);
				}
			}
			
			if(arg3[0].equalsIgnoreCase("search"))
			{
				arg0.sendMessage(Craftcoinish.auc.search( arg3[1] ));
				return true;
			}
			if(arg3[0].equalsIgnoreCase("create"))
			{
				AuctionEntry ae = new AuctionEntry();
				Player play = (Player)arg0;
				error("Found Item",arg0);
				error(play.getItemInHand().getType().name().toLowerCase(),arg0);
				ae.setItemname(play.getItemInHand().getType().name());
				ae.setOwner(arg0.getName());
				ae.setStarted(System.currentTimeMillis());
				ae.setBuyout(Double.parseDouble(arg3[1]));
				ae.setPrice(0);
				ae.setStack(play.getItemInHand().getAmount());
				
				if (plugin == null) {
					Plugin p = Bukkit.getPluginManager().getPlugin("Craftcoinish");
					plugin = (Craftcoinish) p;
				}
				plugin.getDatabase().save(ae);
				
				Player player = Bukkit.getPlayer(arg0.getName());
				ItemStack sx = new ItemStack(Material.matchMaterial(player.getInventory().getItemInHand().getType().name()), ae.getStack());
				player.getInventory().remove(sx);
				
				
				
				
				return true;
			}
		}
		return true;
	}

}
