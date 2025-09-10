package com.johnnyjansen.bank_account.business.dtos.out;


import lombok.*;
import org.bson.types.ObjectId;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class BankAccountResponseDTO {

    private String id;
    private String name;
    private LocalDate birthDate;
    private String email;
    private String phoneNumber;
    private String cpf;


}
