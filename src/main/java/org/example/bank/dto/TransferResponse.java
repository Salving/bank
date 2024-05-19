package org.example.bank.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferResponse {
    private final String username;
    private final BigDecimal newBalance;
}
