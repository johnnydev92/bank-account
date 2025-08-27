package com.johnnyjansen.bank_account.business.dtos.in;


import lombok.*;

import java.time.YearMonth;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class BankAccountDetailsRequestDTO {

    private Long id;
    private String branchNumber;
    private String accountNumber;
    private String cardNumber;
    private String cardHolder;
    private String cvc;
    private YearMonth expirationDate;
    private Long userId;
}
