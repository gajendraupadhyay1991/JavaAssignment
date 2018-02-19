package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransactionDetails;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;

public interface AccountsRepository {

  /**
   * createAccount - To create new account
   * @param account
   * @throws DuplicateAccountIdException
   */
  void createAccount(Account account) throws DuplicateAccountIdException;

  /**
   * getAccount - To get details of an account
   * @param accountId
   * @return Account
   */
  Account getAccount(String accountId);
  
  /**
   * transferBalance - To process transaction within exists accounts
   * @param transferAccount
   */
  void transferBalance(TransactionDetails transferAccount);

  /**
   * clearAccounts - To clear the all accounts
   */
  void clearAccounts();
}
