package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransactionDetails;
import com.db.awmd.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountsService 
{
	/**
	 * AccountsService - To provide services of createAccount, getAccount and transferBalance
	 */

  @Getter
  private final AccountsRepository accountsRepository;
  

  @Autowired
  public AccountsService(AccountsRepository accountsRepository) {
    this.accountsRepository = accountsRepository;
  }
  
  public void createAccount(Account account) 
  {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) 
  {
    return this.accountsRepository.getAccount(accountId);
  }
  
  public void transferBalance(TransactionDetails transaction)
  {
	  this.accountsRepository.transferBalance(transaction);
  }
  
}
