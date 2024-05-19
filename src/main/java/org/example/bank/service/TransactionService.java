package org.example.bank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bank.dto.TransferRequest;
import org.example.bank.dto.TransferResponse;
import org.example.bank.entity.Transaction;
import org.example.bank.entity.UserAccount;
import org.example.bank.exception.InvalidTransactionException;
import org.example.bank.repository.TransactionRepository;
import org.example.bank.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TransferResponse transfer(UserAccount currentUser,TransferRequest transferRequest) {
        String targetUsername = transferRequest.getTargetUsername();
        UserAccount target = userRepository.findByUsername(targetUsername);
        BigDecimal currentAmount = transactionRepository.findAmountByAccount(currentUser);

        log.debug("Transfer request from {}, {}",currentUser.getUsername(), transferRequest);

        if (currentAmount.compareTo(transferRequest.getAmount()) < 0) {
            throw new InvalidTransactionException("Недостаточно средств");
        }

        if (target == null) {
            throw new InvalidTransactionException("Указанного пользователя не существует");
        }

        if (Objects.equals(currentUser.getId(), target.getId())) {
            throw new InvalidTransactionException("Невозможно перевести деньги самому себе");
        }

        Transaction transaction = new Transaction(currentUser, target, transferRequest.getAmount());
        transactionRepository.save(transaction);

        BigDecimal newBalance = transactionRepository.findAmountByAccount(currentUser);
        log.info("Transfer complete from {} to {}",currentUser.getUsername(), transferRequest.getTargetUsername());
        return new TransferResponse(currentUser.getUsername(), newBalance);
    }
}
