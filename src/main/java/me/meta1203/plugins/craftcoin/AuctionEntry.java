package me.meta1203.plugins.craftcoin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name = "cc_auctions")
public class AuctionEntry {
	@Id
    private int id;
	@NotNull
    private String itemname;
	@Column
	private int stack;
	@Column
	private double buyout; 
	@Column
	private double price;
	@Column
	private String Owner;
	@Column
	private long started;
	@Column
	private String Bidder;
	
	public String getBidder()
	{
		return this.Bidder;
	}
	public void setBidder(String Bidder)
	{
		this.Bidder = Bidder;
	}
	public void setItemname(String item)
	{
		this.itemname = item;
	}
	public int getStack()
	{
		return this.stack;
	}
	public void setStack(int Stack)
	{
		this.stack = Stack;
	}
	public void setPrice(double price)
	{
		this.price = price;
	}
	public void setBuyout(double buyout)
	{
		this.buyout = buyout;
	}
	public void setOwner(String owner)
	{
		this.Owner = owner;
	}
	public String getOwner()
	{
		return this.Owner;
	}
	public long getStarted()
	{
		return this.started;
	}
	public void setStarted(long l)
	{
		this.started = l;
	}
	// ID

	// Player Name
	public void setId(int id) {
		this.id = id;
	}	

	// Player Name
	public int getId() {
		return id;
	}
	
	// Player Name
	public String getItemname()
	{
		return this.itemname;
	}	

	// Player Name
	public boolean bid(double amount) {
		if(this.getPrice() < amount)
		{
			this.buyout = amount;
			return true;
		}
		else
		{
			return false;
		}
	}	

	// Player Name
	public double getPrice()
	{
		return this.price;
	}	

	// Player Name
	public double getBuyout()
	{
		return this.buyout;
	}
	// Amount
	
	public void buy()
	{
		Craftcoinish.econ.addFunds(this.Owner, this.buyout);
	}

	
}
