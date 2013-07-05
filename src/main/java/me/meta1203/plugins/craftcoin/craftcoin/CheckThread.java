package me.meta1203.plugins.craftcoin.craftcoin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.litecoin.core.*;
import com.google.litecoin.core.TransactionConfidence.ConfidenceType;
import com.google.litecoin.store.BlockStoreException;

import me.meta1203.plugins.craftcoin.Craftcoinish;
import me.meta1203.plugins.craftcoin.Util;

public class CheckThread extends Thread {
	private List<Transaction> toCheck = new ArrayList<Transaction>();

	private int waitTime = 0;
	private int confirmations = 0;

	public CheckThread(int wait, int confirmations) {
		Craftcoinish.log.info("Checking for " + Integer.toString(confirmations) + " confirmations every " + Integer.toString(wait) + " seconds.");
		waitTime = wait;
		this.confirmations = confirmations;
		List<Transaction> toAdd = Util.loadChecking();
		Craftcoinish.log.info("Adding " + toAdd.size() + " old transactions to the check pool!");
		for (Transaction current : toAdd) {
			Craftcoinish.log.info("Added: " + current.getHashAsString());
			toCheck.add(current);
		}
	}

	public void run() {
		while (true) {
			check();
			try {
				synchronized (this) {
					this.wait(waitTime*1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void addCheckTransaction(Transaction tx) {
		toCheck.add(tx);
		Craftcoinish.log.warning("Added transaction " + tx.getHashAsString() + " to check pool!");
	}

	public synchronized void serialize() {
		Util.serializeChecking(toCheck);
	}

	// Loop checks and outputs

	private void check() {
		synchronized (this) {
			Craftcoinish.log.info("Checking 1"); 
			List<Transaction> toRemove = new ArrayList<Transaction>();
			for (Transaction current : toCheck) {
				
				
				Transaction currents = Craftcoinish.bapi.getWallet().getTransaction(current.getHash());
			
				if (!currents.getConfidence().getConfidenceType().equals(ConfidenceType.BUILDING)) {
					Craftcoinish.log.info("Still building");
					continue;
				}
				int conf = currents.getConfidence().getDepthInBlocks();
				Craftcoinish.log.info(conf + " CONFIRMS");
				if (conf >= confirmations) {
						double value = Craftcoinish.econ.bitcoinToInGame(currents.getValueSentToMe(Craftcoinish.bapi.getWallet()));
						List<Address> receivers = null;
						try {
							Craftcoinish.log.info(currents.getOutputs().toString());
							receivers = Util.getContainedAddress(currents.getOutputs());
						
						} catch (ScriptException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						for (Address x : receivers) {
							String pName = Util.searchAddress(x);
							Craftcoinish.econ.addFunds(pName, value);
							Craftcoinish.log.warning("Added " + Craftcoinish.econ.formatValue(value, true) + " to " + pName + "!");
						}
						
						Craftcoinish.bapi.saveWallet();
					toRemove.add(currents);
				}
			}
			toCheck.removeAll(toRemove);
		}
	}

}
