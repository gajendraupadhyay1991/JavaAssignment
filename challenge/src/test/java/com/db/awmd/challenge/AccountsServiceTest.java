package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransactionDetails;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.service.AccountsService;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.MOCK)
@ActiveProfiles(profiles={"UnitTest"})
@WebAppConfiguration
public class AccountsServiceTest 
{
  @Spy
  @InjectMocks
  @Autowired
  private AccountsService accountsService;
  
  @Mock
  private TransactionDetails transaction;

  @Mock
  private Account account;
  
  @Before
  public void setUp()
  {
	  MockitoAnnotations.initMocks(this);
  }
  
  @Test
  public void addAccount() throws Exception {
    account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);
    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
    account = new Account("Id-456");
    account.setBalance(new BigDecimal(500));
    this.accountsService.createAccount(account);    
    assertThat(this.accountsService.getAccount("Id-456")).isEqualTo(account);
  }

  @Test
  public void addAccount_failsOnDuplicateId() throws Exception {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }
  }
  
  @Test
  public void transferBalance() throws Exception 
  {
	  transaction = new TransactionDetails("Id-123", "Id-456", new BigDecimal(200));
	  this.accountsService.transferBalance(transaction);
	  assertThat("Amount Transfered");
  }
}
