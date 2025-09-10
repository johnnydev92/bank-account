package com.johnnyjansen.bank_account.infrastructure.entities;




import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.YearMonth;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document("Bank-Account-Details")
@Builder
@Document("bank_account_details")
public class BankAccountDetails {

    @Id
 feature/bank-account-desenvolvimento

    private String id;

    private ObjectId id;


    private String id; // MongoDB id é String

    private Long branchNumber;
    private Long accountNumber;
    private Long cardNumber;
    private String cardHolder;
    private Integer cvc;
    private YearMonth expirationDate;

    @DBRef

    private BankAccount user;

    private BankAccount user; // Relacionamento com o dono da conta


}
