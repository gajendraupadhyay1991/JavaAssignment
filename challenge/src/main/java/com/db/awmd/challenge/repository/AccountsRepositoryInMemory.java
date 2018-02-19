package com.db.awmd.challenge.repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransactionDetails;
import com.db.awmd.challenge.exception.AccountDoesNotExistException;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.EmailNotificationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class AccountsRepositoryInMemory implements AccountsRepository 
{
  /**
   * AccountsRepositoryInMemory - To have createAccount, getAccount, transferBalance and clearAccounts
   */
  private final Map<String, Account> accounts = new ConcurrentHashMap<>();

  @Override
  public void createAccount(Account account) throws DuplicateAccountIdException 
  {
	  if(account.getBalance().signum() != -1)
	  {
		Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
		if (previousAccount != null) 
		{
			throw new DuplicateAccountIdException("Account id " + account.getAccountId() + " already exists!");
		}
	  }
	  else
	  {
		  log.error("Negative balance is not allowed!");
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
		  throw new AccountDoesNotExistException("Transaction details does not exists!");
	  }
	  else
	  {
		  if(accounts.containsKey(transferAccount.getAccountToId()) && accounts.containsKey(transferAccount.getAccountFromId()))
		  {
			  transactionProcess(transferAccount);
		  }
		  else
		  {
			  log.error("Account id does not exists!");
		  }
	  }
  }

 /**
  * transactionProcess - To process the transaction within exists accounts
  * @param transferDetails
  */
	private void transactionProcess(TransactionDetails transferDetails) 
	{
		EmailNotificationService emailNotificationService = new EmailNotificationService();
		BigDecimal senderBalance = BigDecimal.ZERO;
		BigDecimal recieverBalance = BigDecimal.ZERO;
		Account recieverAccount = accounts.get(transferDetails.getAccountToId());
		Account senderAccount = accounts.get(transferDetails.getAccountFromId());
		recieverBalance = recieverAccount.getBalance();
		senderBalance = senderAccount.getBalance();
		
		BigDecimal amount = transferDetails.getBalance();
		
		if(senderBalance.compareTo(amount)>=0)
		{
			processTransaction(emailNotificationService, senderBalance, recieverBalance, recieverAccount, senderAccount,
					amount);
		}
		else
		{
			log.error("Transaction failed due to insufficient balance in account {}",senderAccount.getAccountId());
		}
	}

	/**
	 * processTransaction - To process the transfer of balance
	 * @param emailNotificationService
	 * @param senderBalance
	 * @param recieverBalance
	 * @param recieverAccount
	 * @param senderAccount
	 * @param amount
	 */
	private void processTransaction(EmailNotificationService emailNotificationService, BigDecimal senderBalance,
		BigDecimal recieverBalance, Account recieverAccount, Account senderAccount, BigDecimal amount) 
	{
		if(recieverAccount.getAccountId() != senderAccount.getAccountId())
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
			log.error("Duplicates account Id!");
		}
		
	}

  @Override
  public void clearAccounts() {
    accounts.clear();
  }
}
