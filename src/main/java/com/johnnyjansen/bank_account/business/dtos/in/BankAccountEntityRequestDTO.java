package com.johnnyjansen.bank_account.business.dtos.in;


import lombok.*;

import java.time.LocalDate;
import java.time.YearMonth;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class BankAccountEntityRequestDTO {

    private Long id;
    private String name;
    private LocalDate birthDate;
    private String email;
    private String phoneNumber;
    private String cpf;
    private String password;
}
