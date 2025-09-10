package com.johnnyjansen.bank_account.api.tests.request;

import com.johnnyjansen.bank_account.business.dtos.in.BankAccountRequestDTO;
import org.bson.types.ObjectId;

import java.time.LocalDate;

public class BankAccountRequestDtoFixture {

    public static BankAccountRequestDTO build(


        String name,
        LocalDate birthDate,
        String email,
        String phoneNumber,
        String cpf,
        String password


    ) {
        return new BankAccountRequestDTO(name, birthDate, email, phoneNumber, cpf, password);
    }
}
