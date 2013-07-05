package me.meta1203.plugins.craftcoin.craftcoin;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;

import com.google.litecoin.core.*;
import com.google.litecoin.store.BlockStoreException;
import com.google.litecoin.store.SPVBlockStore;

import me.meta1203.plugins.craftcoin.Craftcoinish;

public class CraftcoinAPI {

	private Wallet localWallet;
    private SPVBlockStore localBlock;
    private BlockChain localChain;
	private final File walletFile;
    private PeerGroup localPeerGroup = null;
    public final BigInteger minBitFee = BigInteger.valueOf((long)(0.0005*Math.pow(10, 8)));
	
	public CraftcoinAPI() {
		walletFile = new File("plugins/Craftcoinish/wallet.wallet");
		try {
		    localWallet = Wallet.loadFromFile(walletFile);
		    // Satoshis.log.info(localWallet.toString());
		} catch (IOException e) {
            localWallet = new Wallet(Craftcoinish.network);
		}
		try {
            localBlock = new SPVBlockStore(Craftcoinish.network, new File("plugins/Craftcoinish/h2.blockchain"));
            localChain = new BlockChain(Craftcoinish.network, localWallet, localBlock);
		} catch (BlockStoreException ex) {
			ex.printStackTrace();
		}
        localWallet.addEventListener(new CoinListener());
        localPeerGroup = new PeerGroup(Craftcoinish.network, localChain);
        localPeerGroup.setUserAgent("CraftcoinBukkit", "0.2");
        localPeerGroup.addWallet(localWallet);
        try {
			localPeerGroup.addAddress(new PeerAddress(InetAddress.getByName("smp1.spendlitecoins.com"), 12124));
			localPeerGroup.addAddress(new PeerAddress(InetAddress.getByName("207.68.215.202"), 12124));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
		}
        localPeerGroup.start();
        localPeerGroup.downloadBlockChain();
        try {
			StoredBlock b = localBlock.get(new Sha256Hash("64a9141746cbbe06c7e1a4b7f2abb968ccdeba66cd67c1add1091b29db00578e"));
			
			
			for (Transaction tx : localWallet.getTransactions(false, true)) {
				
			}
			System.out.println("Good TX's");
			for (Transaction tx : localWallet.getTransactionsByTime()) {
			
			}
		} catch (BlockStoreException e) {
			e.printStackTrace();
		}
	}

	public PeerGroup getLocalPeerGroup() {
		return localPeerGroup;
	}

	public void setLocalPeerGroup(PeerGroup localPeerGroup) {
		this.localPeerGroup = localPeerGroup;
	}

	public Address genAddress() {
		ECKey key = new ECKey();
		localWallet.addKey(key);
		return key.toAddress(Craftcoinish.network);
	}

	@Override
	protected void finalize() throws Throwable {
        localWallet.saveToFile(new File("plugins/Craftcoinish/wallet.wallet"));
	}
	
	public boolean localSendCoins(Address a, double value) {
        BigInteger sendAmount = Craftcoinish.econ.inGameToBitcoin(value);
        
        Wallet.SendRequest request = Wallet.SendRequest.to(a, sendAmount);
        request.fee = minBitFee;
        
        if (!localWallet.completeTx(request))
        	return false;
        localPeerGroup.broadcastTransaction(request.tx);
        try {
		if (!localWallet.completeTx(request)) {
			return false;
		} else {
			localPeerGroup.broadcastTransaction(request.tx);
			try {
				localWallet.commitTx(request.tx);
			} catch (VerificationException e) {
				
			}
		}
        }
		catch (IllegalArgumentException x)
		{
			
		}
			Craftcoinish.log.warning("Sent transaction: " + request.tx.getHash());
			saveWallet();
			return true;

	}
	
	public boolean sendCoinsMulti(Map<Address, Double> toSend) {
		Transaction tx = new Transaction(Craftcoinish.network);
		double totalSend = 0.0;
		
		for (Entry<Address, Double> current : toSend.entrySet()) {
			totalSend += current.getValue() / Craftcoinish.mult;
			tx.addOutput(Craftcoinish.econ.inGameToBitcoin(current.getValue()), current.getKey());
		}
		
		if (totalSend < 0.01) {
			return false;
		}
		
		Wallet.SendRequest request = Wallet.SendRequest.forTx(tx);
		
		if (!localWallet.completeTx(request)) {
			return false;
		} else {
			localPeerGroup.broadcastTransaction(request.tx);
			try {
				localWallet.commitTx(request.tx);
			} catch (VerificationException e) {
				e.printStackTrace();
			}
			return true;
		}
	}
	
	
	public void saveWallet() {
		try {
            localWallet.saveToFile(walletFile);
           // localPeerGroup.stop();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

    public Wallet getWallet() {
        return localWallet;
    }

    public BlockChain getChain() {
        return localChain;
    }
    
    public void reloadWallet() {
    	localPeerGroup.stop();
    	localWallet.clearTransactions(0);
    	new File("plugins/Craftcoinish/spv.blockchain").delete();
    	localPeerGroup.start();
    	localPeerGroup.downloadBlockChain();
    }

	public SPVBlockStore getLocalBlock() {
		return localBlock;
	}
}
