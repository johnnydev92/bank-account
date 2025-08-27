package com.johnnyjansen.bank_account.business.dtos.out;


import lombok.*;

import java.time.LocalDate;
import java.time.YearMonth;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class BankAccountEntityResponseDTO {

    private Long id;
    private String name;
    private LocalDate birthDate;
    private String email;
    private String phoneNumber;
    private String cpf;

}
