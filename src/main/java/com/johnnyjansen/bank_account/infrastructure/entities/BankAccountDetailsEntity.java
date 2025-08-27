package com.johnnyjansen.bank_account.infrastructure.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bank_details")
@Builder
public class BankAccountDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "branch_number", unique = true)
    private Long branchNumber;

    @Column(name = "account_number", unique = true)
    private Long accountNumber;

    @Column(name = "card_number", unique = true)
    private Long cardNumber;

    @Column(name = "card_holder")
    private String cardHolder;

    @Column(name = "cvc")
    private Integer cvc ;

    @Column(name = "expiration_Date")
    private LocalDate expirationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private BankAccountEntity user;

}
