package org.example.bank.service;

import org.example.bank.dto.TransferRequest;
import org.example.bank.dto.TransferResponse;
import org.example.bank.entity.Transaction;
import org.example.bank.entity.UserAccount;
import org.example.bank.exception.InvalidTransactionException;
import org.example.bank.repository.TransactionRepository;
import org.example.bank.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

class TransactionServiceTests {

    private UserRepository userRepository;
    private TransactionRepository transactionRepository;
    private TransactionService transactionService;

    private List<UserAccount> testAccounts;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        transactionService = new TransactionService(transactionRepository, userRepository);

        testAccounts = List.of(
                new UserAccount(1L, "fromAccount", "password", "", LocalDate.now(), List.of("mail@mail.test"),
                        List.of("9267453900")),
                new UserAccount(2L, "toAccount", "password", "", LocalDate.now(), List.of("mail2@mail.test"),
                        List.of("9267453901")));
    }

    @Test
    public void testValidTransfer() {
        UserAccount fromAccount = testAccounts.get(0);
        UserAccount toAccount = testAccounts.get(1);
        TransferRequest transferRequest = new TransferRequest("toAccount", BigDecimal.valueOf(100));
        when(transactionRepository.findAmountByAccount(fromAccount)).thenReturn(BigDecimal.valueOf(100))
                .thenReturn(BigDecimal.valueOf(0));
        when(userRepository.findByUsername("toAccount")).thenReturn(toAccount);

        TransferResponse transferResponse = transactionService.transfer(fromAccount, transferRequest);

        Transaction transaction = new Transaction(fromAccount, toAccount, BigDecimal.valueOf(100));
        verify(transactionRepository, times(1)).save(transaction);
        Assertions.assertEquals(new TransferResponse("fromAccount", BigDecimal.valueOf(0)), transferResponse);
    }

    @Test
    public void notEnoughBalanceThrowsException() {
        UserAccount fromAccount = testAccounts.get(0);
        UserAccount toAccount = testAccounts.get(1);
        TransferRequest transferRequest = new TransferRequest("toAccount", BigDecimal.valueOf(100));
        when(transactionRepository.findAmountByAccount(fromAccount)).thenReturn(BigDecimal.valueOf(0));
        when(userRepository.findByUsername("toAccount")).thenReturn(toAccount);

        Assertions.assertThrows(InvalidTransactionException.class, () -> transactionService.transfer(fromAccount, transferRequest));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    public void nullTargetAccountThrowsException() {
        UserAccount fromAccount = testAccounts.get(0);
        TransferRequest transferRequest = new TransferRequest("toAccount", BigDecimal.valueOf(100));
        when(transactionRepository.findAmountByAccount(fromAccount)).thenReturn(BigDecimal.valueOf(100));
        when(userRepository.findByUsername("toAccount")).thenReturn(null);

        Assertions.assertThrows(InvalidTransactionException.class, () -> transactionService.transfer(fromAccount, transferRequest));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    public void selfTransferThrowsException() {
        UserAccount fromAccount = testAccounts.get(0);
        TransferRequest transferRequest = new TransferRequest("fromAccount", BigDecimal.valueOf(100));
        when(transactionRepository.findAmountByAccount(fromAccount)).thenReturn(BigDecimal.valueOf(100));
        when(userRepository.findByUsername("fromAccount")).thenReturn(fromAccount);

        Assertions.assertThrows(InvalidTransactionException.class, () -> transactionService.transfer(fromAccount, transferRequest));

        verify(transactionRepository, never()).save(any());
    }
}