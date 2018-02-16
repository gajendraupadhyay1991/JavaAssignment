package com.db.awmd.challenge.repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transaction;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
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
  public void transferBalance(Transaction transferAccount) 
  {  	
	  if(transferAccount == null)
	  {
		  log.error("Transaction detail is null!");
	  }
	  else
	  {
		  if(accounts.containsKey(transferAccount.getAccountToId()))
		  {
			  BigDecimal balance = BigDecimal.ZERO;
			  Account account = accounts.get(transferAccount.getAccountToId());
			  balance = account.getBalance();
			  BigDecimal amount = transferAccount.getBalance();
			  account.setBalance(balance.add(amount));
			  accounts.replace(account.getAccountId(), account);
		  }
		  else
		  {
			  log.error("Transfer account does not exist!");
		  }
	  }
  }

  @Override
  public void clearAccounts() {
    accounts.clear();
  }



}
