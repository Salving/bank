package org.example.bank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bank.entity.Transaction;
import org.example.bank.repository.TransactionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterestService {
    private final TransactionRepository transactionRepository;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    public void processInterest() {
        log.info("Processing interest");

        transactionRepository.findAllBalances()
                .forEach(accountBalance -> {
                    log.info("Processing account: {}", accountBalance.getAccount()
                            .getId());
                    log.info("Account balance: {}", accountBalance.getBalance());

                    BigDecimal balance = accountBalance.getBalance();
                    BigDecimal startSum = accountBalance.getStartSum();
                    BigDecimal limit = startSum.multiply(BigDecimal.valueOf(2.07));

                    if (balance.compareTo(limit) < 0) {
                        BigDecimal newBalance = balance.multiply(BigDecimal.valueOf(1.05));
                        if (newBalance.compareTo(limit) > 0) newBalance = limit;

                        BigDecimal interest = newBalance.subtract(balance);
                        log.info("Interest: {}", interest);

                        Transaction transaction = new Transaction(accountBalance.getAccount(), interest);
                        transactionRepository.save(transaction);
                    }
                });
    }
}
