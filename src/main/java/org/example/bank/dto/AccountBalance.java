package org.example.bank.dto;

import lombok.Data;
import org.example.bank.entity.UserAccount;

import java.math.BigDecimal;

@Data
public class AccountBalance {
    private final UserAccount account;
    private final BigDecimal balance;
    private final BigDecimal startSum;
}
