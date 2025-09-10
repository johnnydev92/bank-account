package com.johnnyjansen.bank_account.business.dtos.in;


import lombok.*;
import org.bson.types.ObjectId;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class BankAccountRequestDTO {

    private String name;
    private LocalDate birthDate;
    private String email;
    private String phoneNumber;
    private String cpf;
    private String password;
}
