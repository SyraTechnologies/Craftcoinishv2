package me.meta1203.plugins.craftcoin.craftcoin;

import java.math.BigInteger;
import java.util.List;

import me.meta1203.plugins.craftcoin.Craftcoinish;
import me.meta1203.plugins.craftcoin.Util;

import com.google.litecoin.core.*;
import com.google.litecoin.core.TransactionConfidence.ConfidenceType;
import com.google.litecoin.store.BlockStoreException;
import com.google.litecoin.core.Address;


public class CoinListener extends AbstractWalletEventListener {

	@Override
	public void onCoinsReceived(Wallet wallet, Transaction tx,
			BigInteger prevBalance, BigInteger newBalance) {
		
		
		BigInteger CRCAdded = newBalance.subtract(prevBalance); 
		BigInteger CRCAddedc = BigInteger.ZERO;
		if(CRCAdded.compareTo(CRCAddedc) != -1)
		{
				Craftcoinish.checker.addCheckTransaction(tx);
		}
		
		

	
		
	}

}
