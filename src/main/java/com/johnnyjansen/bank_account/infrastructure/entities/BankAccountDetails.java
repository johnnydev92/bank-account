package com.johnnyjansen.bank_account.infrastructure.entities;




import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document("Bank-Account-Details")
@Builder
@Document("bank_account_details")
public class BankAccountDetails {

    @Id

    private String id;

    private String id; // MongoDB id é String

    private Long branchNumber;
    private Long accountNumber;
    private Long cardNumber;
    private String cardHolder;
    private Integer cvc;
    private LocalDate expirationDate;

    @DBRef

    private BankAccount user;

    private BankAccount user; // Relacionamento com o dono da conta


}
