package com.johnnyjansen.bank_account.api.tests.mappers;


import com.johnnyjansen.bank_account.api.tests.request.BankAccountRequestDtoFixture;
import com.johnnyjansen.bank_account.api.tests.response.BankAccountResponseDtoFixture;
import com.johnnyjansen.bank_account.business.dtos.in.BankAccountDetailsRequestDTO;
import com.johnnyjansen.bank_account.business.dtos.in.BankAccountRequestDTO;
import com.johnnyjansen.bank_account.business.dtos.out.BankAccountDetailsResponseDTO;
import com.johnnyjansen.bank_account.business.dtos.out.BankAccountResponseDTO;
import com.johnnyjansen.bank_account.business.mapper.BankAccountMapperConverter;
import com.johnnyjansen.bank_account.business.mapper.BankAccountUpdateMapperConverter;
import com.johnnyjansen.bank_account.infrastructure.entities.BankAccount;
import com.johnnyjansen.bank_account.infrastructure.entities.BankAccountDetails;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class BankAccountMapperConverterTest {


    BankAccountMapperConverter bankAccountMapperConverter;

    BankAccountUpdateMapperConverter bankAccountUpdateMapperConverter;

    BankAccount bankAccount;

    BankAccountDetails bankAccountDetails;

    BankAccountRequestDTO bankAccountRequestDTO;

    BankAccountDetailsRequestDTO bankAccountDetailsRequestDTO;

    BankAccountResponseDTO bankAccountResponseDTO;

    BankAccountDetailsResponseDTO bankAccountDetailsResponseDTO;


    @BeforeEach
    public void setup() {

        bankAccountMapperConverter = Mappers.getMapper(BankAccountMapperConverter.class);
        bankAccountUpdateMapperConverter = Mappers.getMapper(BankAccountUpdateMapperConverter.class);


        bankAccountDetails = BankAccountDetails.builder()
                .id(new ObjectId())
                .branchNumber(1234L)
                .accountNumber(458714256L)
                .cardNumber(2047875414359547L)
                .cvc(989)
                .cardHolder("JOANA DARK")
                .expirationDate(YearMonth.of(2031, 4))
                .user(null)
                .build();

        bankAccount = BankAccount.builder()
                .id(new ObjectId())
                .name("Joana Dark")
                .email("joana.dark@hotmail.com")
                .password("123456")
                .cpf("05564512300")
                .phoneNumber("+55(98)87669548")
                .birthDate(LocalDate.of(1990, 5, 20))
                .bankAccountDetailsEntities(List.of(bankAccountDetails))
                .build();

        bankAccountRequestDTO = BankAccountRequestDtoFixture.build
                ("Joana Dark",
                        LocalDate.of(1990, 5, 20),
                        "joana.dark@hotmail.com",
                        "+55(98)87669548",
                        "05564512300",
                        "123456");


        bankAccountResponseDTO = BankAccountResponseDtoFixture.build
                (bankAccount.getId() != null ? bankAccount.getId().toHexString() : null
                        ,"Joana Dark",
                        LocalDate.of(1990,5,20),
                        "joana.dark@hotmail.com",
                        "+55(98)87669548",
                        "05564512300");

        bankAccountDetails.setUser(bankAccount);

    }

    @Test
    void mustMapBankAccountToBankAccountResponseDto() {

        BankAccountResponseDTO dto = bankAccountMapperConverter.toBankAccountResponseDTO(bankAccount);

        assertEquals(bankAccountResponseDTO, dto);


    }

    @Test
    void mustMapBankAccountToBankAccountRequestDto(){

        BankAccountRequestDTO dto = bankAccountMapperConverter.toBankAccountRequestDTO(bankAccount);

        assertEquals(bankAccountRequestDTO, dto);

    }

    @Test
    void mustMapBankAccountRequestDtoToBankAccount(){

        BankAccount entity = bankAccountMapperConverter.toBankAccount(bankAccountRequestDTO);

        assertEquals(bankAccount, entity);
    }

    @Test
    void mustMapBankAccountDetailsToBankAccountDetailsResponseDto(){

        BankAccountDetailsResponseDTO dto = bankAccountMapperConverter
                .toBankAccountDetailsResponseDTO(bankAccountDetails);

        assertEquals(bankAccountDetailsResponseDTO, dto);

    }

    @Test
    void mustMapBankAccountDetailsRequestDtoToBankAccountDetails(){



    }

}
