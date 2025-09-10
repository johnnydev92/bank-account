package com.johnnyjansen.bank_account.business.dtos.out;


import lombok.*;
import org.bson.types.ObjectId;

import java.time.YearMonth;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class BankAccountDetailsResponseDTO {

    private String id;
    private String branchNumber;
    private String accountNumber;
    private String cardNumber;
    private String cardHolder;
    private String cvc;
    private YearMonth expirationDate;
    private Long userId;
}
