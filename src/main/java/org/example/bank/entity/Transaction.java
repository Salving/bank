package org.example.bank.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue
    private long id;

    /**
     * Может быть пустым для начисления начальной суммы/процентов
     */

    @ManyToOne
    private UserAccount fromAccount;

    @ManyToOne(optional = false)
    @NonNull
    private UserAccount toAccount;

    @Column(nullable = false)
    @NonNull
    private BigDecimal amount;

    @CreationTimestamp
    private Date date;

    public Transaction(UserAccount fromAccount, @NonNull UserAccount toAccount, @NonNull BigDecimal amount) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
    }
}
