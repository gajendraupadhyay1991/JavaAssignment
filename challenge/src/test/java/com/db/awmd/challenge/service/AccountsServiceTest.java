package com.db.awmd.challenge.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransactionDetails;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.AccountsService;


import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.MOCK)
@ActiveProfiles(profiles={"UnitTest"})
@WebAppConfiguration
public class AccountsServiceTest 
{

  @SpyBean
  private AccountsService accountsService;
  
  @Mock
  private TransactionDetails transaction;

  @Mock
  private Account account, account2;
  
  @Before
  public void setUp()
  {
	  MockitoAnnotations.initMocks(this);
  }
  
  @Test
  public void addAccount() throws Exception 
  {
    account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    accountsService.createAccount(account);
    assertThat("Account first inserted!");
  }

  @Test
  public void addAccount_failsOnDuplicateId() throws Exception {
    String uniqueId = "Id-456";
    Account account = new Account(uniqueId);
    account.setBalance(new BigDecimal(1000));
    accountsService.createAccount(account);
    try 
    {
      accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } 
    catch (DuplicateAccountIdException ex) 
    {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }
  }
  
  @Test
  public void transferBalance() throws Exception 
  {
	  transaction = new TransactionDetails("Id-123", "Id-456", new BigDecimal(200));
	  accountsService.transferBalance(transaction);
	  assertThat("Amount Transfered");
  }
}
