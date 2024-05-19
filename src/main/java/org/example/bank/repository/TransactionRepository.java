package org.example.bank.repository;

import org.example.bank.dto.AccountBalance;
import org.example.bank.entity.Transaction;
import org.example.bank.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("select t from Transaction t where :account = t.toAccount or :account = t.fromAccount")
    List<Transaction> findByAccount(UserAccount account);

    @Query("select sum(case when (:account = t.toAccount) then t.amount else 0 - t.amount end) from Transaction t where :account = t.toAccount or :account = t.fromAccount")
    BigDecimal findAmountByAccount(UserAccount account);

    @Query("""
            select new org.example.bank.dto.AccountBalance(
                u,
                sum(case when (u = t.toAccount) then t.amount else 0 - t.amount end),
                (select t.amount from Transaction t where t.toAccount = u order by t.date limit 1)
            )
            from UserAccount u
            join Transaction t on t.toAccount = u or t.fromAccount = u
            group by u
            """)
    Stream<AccountBalance> findAllBalances();
}
