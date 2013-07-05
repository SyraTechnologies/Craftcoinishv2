Craftcoin
========

The one and only cross-server minecraft economy.  

Commands:  

/money - List current amount of Craftcoin in your minecraft account.  
/transact <player> <amount> - Transfer's money from your account to the selected player's account.  
/deposit - Get a Craftcoin address to send a deposit to. The next transaction to that address will fund your account.  
/withdraw <address> [amount] - Transfers money from your account to your Craftcoin wallet. Must have at least the amount specified in the config. If the amount is left off, it will transfer all of your funds.  
/admin info - Print basic debuging and economy info.  
/admin reset - Delete and re-download the block chain.  
/syscheck - Verify that the current Craftcoin holdings tally with the balances of all in-game accounts.  
/credit <player> <amount> - Add the specified amount to the given player's balance  
/debit <player> <amount> - Subtract the specified amount from the given player's balance  

Jenkins: https://meta1203.ci.cloudbees.com/job/Craftcoinish/

Permissions:
craftcoin.* - All commands  
craftcoin.money - /money  
craftcoin.transact - /transact  
craftcoin.withdraw - /withdraw  
craftcoin.admin - /admin  
craftcoin.info - /syscheck  
craftcoin.credit - /credit  
craftcoin.debit - /debit
