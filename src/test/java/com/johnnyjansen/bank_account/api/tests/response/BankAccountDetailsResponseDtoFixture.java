package com.johnnyjansen.bank_account.api.tests.response;

import com.johnnyjansen.bank_account.business.dtos.out.BankAccountDetailsResponseDTO;
import org.bson.types.ObjectId;

import java.time.YearMonth;

public class BankAccountDetailsResponseDtoFixture {

    public static BankAccountDetailsResponseDTO build(

             String id,
             String branchNumber,
             String accountNumber,
             String cardNumber,
             String cardHolder,
             String cvc,
             YearMonth expirationDate,
             String userId

    ){

        return new BankAccountDetailsResponseDTO
                (id, branchNumber, accountNumber, cardNumber, cardHolder, cvc, expirationDate, userId);
    }

}
