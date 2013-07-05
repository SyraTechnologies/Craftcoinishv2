package me.meta1203.plugins.craftcoin.craftcoin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.avaje.ebean.Ebean;
import com.google.litecoin.core.*;
import com.google.litecoin.core.TransactionConfidence.ConfidenceType;
import com.google.litecoin.store.BlockStoreException;

import me.meta1203.plugins.craftcoin.AuctionEntry;
import me.meta1203.plugins.craftcoin.Craftcoinish;
import me.meta1203.plugins.craftcoin.Util;

public class AuctionsThread extends Thread {
	private List<Transaction> toCheck = new ArrayList<Transaction>();

	private int waitTime = 60;

	public AuctionsThread() {
		
	}
	

	public void run() {
		while (true) {
			checkauctions();
			try {
				synchronized (this) {
					this.wait(waitTime*1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


	// Loop checks and outputs

	private void checkauctions() {
		synchronized (this) {
			Craftcoinish.log.info("Checking 2"); 
			String returns = "";
				Craftcoinish p = (Craftcoinish)Bukkit.getPluginManager().getPlugin("Craftcoinish");

			returns = "Items Found:";
			List<AuctionEntry> ae = p.getDatabase().find(AuctionEntry.class).where().isNotNull("id").findList();
			
			for (AuctionEntry x : ae) {
				int xx = (int) (System.currentTimeMillis()/100 - x.getStarted());
			
				if(xx < 86400*Craftcoinish.auction_days)
				{
					try
					{
					if(Bukkit.getPlayer(x.getBidder()) != null)
					{
						
						Bukkit.getPlayer(x.getBidder()).sendMessage(ChatColor.RED + "You have won the auction ");
						ItemStack sx = new ItemStack(Material.matchMaterial(x.getItemname()), x.getStack());
						Bukkit.getPlayer(x.getBidder()).getInventory().addItem(sx);
						Bukkit.getPlayer(x.getBidder()).updateInventory();
						
						p.getDatabase().delete(x);
					}
					}
					catch(Exception e)
					{
					}
					}
				}
			}
		}
	}
