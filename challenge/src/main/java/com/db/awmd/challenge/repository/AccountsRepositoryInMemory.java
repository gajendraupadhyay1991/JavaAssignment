package com.db.awmd.challenge.repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransactionDetails;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.EmailNotificationService;
import com.db.awmd.challenge.web.AccountsController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class AccountsRepositoryInMemory implements AccountsRepository 
{
  private final Map<String, Account> accounts = new ConcurrentHashMap<>();

  @Override
  public void createAccount(Account account) throws DuplicateAccountIdException 
  {
    Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
    if (previousAccount != null) 
    {
    	throw new DuplicateAccountIdException("Account id " + account.getAccountId() + " already exists!");
    }
  }

  @Override
  public Account getAccount(String accountId) {
    return accounts.get(accountId);
  }
  
  @Override
  public void transferBalance(TransactionDetails transferAccount) 
  {  	
	  if(transferAccount == null)
	  {
		  log.error("Transaction detail is null!");
	  }
	  else
	  {
		  if(accounts.containsKey(transferAccount.getAccountToId()) || accounts.containsKey(transferAccount.getAccountFromId()))
		  {
			  transactionProcess(transferAccount);
		  }
		  else
		  {
			  log.error("Transfer account does not exist!");
		  }
	  }
  }

	private void transactionProcess(TransactionDetails transferAccount) 
	{
		EmailNotificationService emailNotificationService = new EmailNotificationService();
		BigDecimal senderBalance = BigDecimal.ZERO;
		BigDecimal recieverBalance = BigDecimal.ZERO;
		Account recieverAccount = accounts.get(transferAccount.getAccountToId());
		Account senderAccount = accounts.get(transferAccount.getAccountFromId());
		
		recieverBalance = recieverAccount.getBalance();
		senderBalance = senderAccount.getBalance();
		
		BigDecimal amount = transferAccount.getBalance();
		
		if(senderBalance.compareTo(amount)>=0)
		{
			recieverAccount.setBalance(recieverBalance.add(amount));
			senderAccount.setBalance(senderBalance.subtract(amount));
			accounts.replace(senderAccount.getAccountId(), senderAccount);
			emailNotificationService.notifyAboutTransfer(recieverAccount, String.valueOf(amount));
			accounts.replace(recieverAccount.getAccountId(), recieverAccount);
			emailNotificationService.notifyAboutTransfer(senderAccount, String.valueOf(amount));
			log.info("Transaction is done successfully!");
		}
		else
		{
			log.error("Transaction failed due to insufficient balance in account {}",senderAccount.getAccountId());
		}
	}

  @Override
  public void clearAccounts() {
    accounts.clear();
  }



}
