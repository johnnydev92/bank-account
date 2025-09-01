package com.johnnyjansen.bank_account.business.dtos.in;


import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class BankAccountRequestDTO {

    private Long id;
    private String name;
    private LocalDate birthDate;
    private String email;
    private String phoneNumber;
    private String cpf;
    private String password;
}
