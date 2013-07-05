package me.meta1203.plugins.craftcoin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import me.meta1203.plugins.craftcoin.commands.*;
import me.meta1203.plugins.craftcoin.craftcoin.AuctionsThread;
import me.meta1203.plugins.craftcoin.craftcoin.CheckThread;
import me.meta1203.plugins.craftcoin.craftcoin.CraftcoinAPI;
import me.meta1203.plugins.craftcoin.database.DatabaseScanner;
import me.meta1203.plugins.craftcoin.database.SystemCheckThread;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.io.IOException;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.mcstats.Metrics;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.block.SignChangeEvent;
import com.google.litecoin.core.NetworkParameters;
import org.bukkit.inventory.ItemStack;
public class Craftcoinish extends JavaPlugin implements Listener {
	// Plugin
	public static int auction_days = 2;
	public static String owner = "";
	public static String currencyName = "";
	public static double tax = 0.0;
	public static boolean buyerorseller = false;
	public static boolean salesTax = false;
	public static double mult = 0;
	public static int confirms = 2;
	public static double minWithdraw = 0;
	public static CraftcoinAPI bapi = null;
	public static CheckThread checker = null;
	public static Logger log = null;
	public static SatoshisEconAPI econ = null;
	public static DatabaseScanner scanner = null;
	public static AuctionsThread auctions_thread = null;
	public static NetworkParameters network = null;
	private SystemCheckThread syscheck = null;
	public static Auctions auc = null;
	
    public void onDisable() {
    	checker.serialize();
    	bapi.saveWallet();
    }

    public void onEnable() {
    	log = getLogger();
    	setupDatabase();
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	saveConfig();
    	owner = config.getString("craftcoinish.owner");
    	currencyName = config.getString("craftcoinish.currency-name");
    	tax = config.getDouble("craftcoinish.tax");
    	
    	buyerorseller = config.getBoolean("craftcoinish.is-buyer-responsible");
    	salesTax = config.getBoolean("craftcoinish.sales-tax");
    	minWithdraw = config.getDouble("craftcoin.min-withdraw");
    	mult = config.getDouble("craftcoinish.multiplier");
    	network = NetworkParameters.prodNet();
    	confirms = config.getInt("craftcoin.confirms");
    	auction_days = config.getInt("auction.days");
    	// Config loading done!
    	log.info("Satoshis configuration loaded.");
    	auc = new Auctions();
    	checker = new CheckThread(config.getInt("craftcoin.check-interval"), confirms);
    	auctions_thread = new AuctionsThread();
    	syscheck = new SystemCheckThread(config.getInt("self-check.delay"), config.getBoolean("self-check.startup"));
    	econ = new SatoshisEconAPI();
    	econ.buyerorseller = buyerorseller;
    	bapi = new CraftcoinAPI();
    	scanner = new DatabaseScanner(this);
    	checker.start();
    	syscheck.start();
    	auctions_thread.start();
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("deposit").setExecutor(new DepositCommand());
        this.getCommand("withdraw").setExecutor(new WithdrawCommand());
        this.getCommand("money").setExecutor(new MoneyCommand());
        this.getCommand("syscheck").setExecutor(new CheckCommand());
        this.getCommand("transact").setExecutor(new SendCommand());
        this.getCommand("credit").setExecutor(new CreditCommand());
        this.getCommand("debit").setExecutor(new DebitCommand());
        this.getCommand("craftcoin").setExecutor(new AdminCommand());
        this.getCommand("auction").setExecutor(new AuctionsCommand());
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
            log.info("Metrics started!");
        } catch (IOException e) {
        	log.info("Metrics disabled.");
        }
    
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
        Util.saveAccount(Util.loadAccount(event.getPlayer().getName()));
    }

	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		list.add(AccountEntry.class);
		list.add(AuctionEntry.class);
		return list;
	}

	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		return true;
	}
	
	private void setupDatabase() {
        try {
        	
            getDatabase().find(AccountEntry.class).findRowCount();
        } catch (PersistenceException ex) {
            log.info("Installing database for " + getDescription().getName() + " due to first time usage");
            installDDL();
        }
    }
	
	public AccountEntry getAccount(String name) {
		return getDatabase().find(AccountEntry.class).where().ieq("playerName", name).findUnique();
	}
	
	public void saveAccount(AccountEntry ae) {
		getDatabase().save(ae);
	}
	


	@EventHandler
    public void onSignCreate(SignChangeEvent event) {
        Player Player = event.getPlayer();
        if(event.getPlayer().isOp())
        {
        if(event.getLine(0).equalsIgnoreCase("[buy]")) {
            Player.sendMessage("You created me");
            event.setLine(0, ChatColor.DARK_BLUE+"[Buy]");
        }
        }
    }
	 @SuppressWarnings("deprecation")
	@EventHandler
	 public void onClick(PlayerInteractEvent e)
	 {
		 if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
	 	{
		Block block = e.getClickedBlock();
	 	Player p = e.getPlayer();
	 	
	 	if (block.getState() instanceof Sign) {
	 		Sign sign = (Sign) block.getState();
	 		String name = sign.getLine(0);
	 		if(name.equalsIgnoreCase(ChatColor.DARK_BLUE+"[Buy]")) {
	 		
	 		double ball = econ.getMoney(p.getName());
	 		double ball2 = Double.parseDouble(sign.getLine(3).replace("$", ""));
	 		if(ball >= ball2)
	 		{
	 			
	 		if(Material.matchMaterial(sign.getLine(2)) != null)
 				{
 					ItemStack x = new ItemStack(Material.matchMaterial(sign.getLine(2)), Integer.parseInt(sign.getLine(1)));
 					p.getInventory().addItem(x);
 					p.updateInventory();
 					p.sendMessage("You just purchased " + sign.getLine(1) + " " + sign.getLine(2));
 					econ.subFunds(p.getName(), Double.parseDouble(sign.getLine(3).replace("$", "")));
 				}
	 		else
	 		{
	 			p.sendMessage(sign.getLine(2) + " is not a valid item!");
	 		}
	 		}
	 		else
	 		{
	 			p.sendMessage("You can't afford this!");
	 		}
	 		}
	 		}
	 	}
}
	@EventHandler
	public void playerLogin(PlayerLoginEvent e) {
		saveAccount(Util.loadAccount(e.getPlayer().getName()));
	}
}
