package com.db.awmd.challenge.domain;

import com.db.awmd.challenge.repository.AccountsRepository;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.NotEmpty;

@Data
public class TransactionDetails {

  @NotNull
  @NotEmpty
  private final String accountFromId;

  @NotNull
  @NotEmpty
  private final String accountToId;
    
  @NotNull
  @Min(value = 0, message = "Initial balance must be positive.")
  private BigDecimal balance;

  public TransactionDetails(String accountFromId, String accountToId) 
  {
    this.accountFromId = accountFromId;
    this.accountToId = accountToId;
    this.balance = BigDecimal.ZERO;
  }

  @JsonCreator
  public TransactionDetails(@JsonProperty("accountFromId") String accountFromId,
  @JsonProperty("accountToId") String accountToId, @JsonProperty("balance") BigDecimal balance) 
  {
	  this.accountFromId = accountFromId;
	  this.accountToId = accountToId;
	  this.balance = balance;
  }
}
