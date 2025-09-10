package com.johnnyjansen.bank_account.api.tests.response;

import com.johnnyjansen.bank_account.business.dtos.out.BankAccountResponseDTO;
import org.bson.types.ObjectId;

import java.time.LocalDate;

public class BankAccountResponseDtoFixture {

    public static BankAccountResponseDTO build(

             String id,
            String name,
            LocalDate birthDate,
            String email,
            String phoneNumber,
            String cpf
    ){
        return new BankAccountResponseDTO(id, name, birthDate, email, phoneNumber, cpf);
    }
}
