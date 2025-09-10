package com.johnnyjansen.bank_account.api.tests.request;

import com.johnnyjansen.bank_account.business.dtos.in.BankAccountDetailsRequestDTO;
import org.bson.types.ObjectId;

import java.time.YearMonth;

public class BankAccountDeatailsRequestDtoFixture {

        public static BankAccountDetailsRequestDTO build (


            String branchNumber,
            String accountNumber,
            String cardNumber,
            String cardHolder,
            String cvc,
            YearMonth expirationDate,
            Long userId

        ){
            return new BankAccountDetailsRequestDTO
                    (branchNumber, accountNumber, cardNumber, cardHolder, cvc, expirationDate, userId);
        }

}
