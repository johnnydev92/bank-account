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

import static org.assertj.core.api.Assertions.assertThat;
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

        bankAccount = BankAccount.builder()
                .id(new ObjectId())
                .name("Joana Dark")
                .email("joana.dark@hotmail.com")
                .password("123456")
                .cpf("05564512300")
                .phoneNumber("+55(98)87669548")
                .birthDate(LocalDate.of(1990, 5, 20))
                .build();


        bankAccountDetails = BankAccountDetails.builder()
                .id(new ObjectId())
                .branchNumber(1234L)
                .accountNumber(458714256L)
                .cardNumber(2047875414359547L)
                .cvc(989)
                .cardHolder("JOANA DARK")
                .expirationDate(YearMonth.of(2031, 4))
                .user(bankAccount)
                .build();


        bankAccount.setBankAccountDetailsEntities(List.of(bankAccountDetails));


        bankAccountDetailsResponseDTO = BankAccountDetailsResponseDTO.builder()
                .id(bankAccountDetails.getId().toHexString())
                .branchNumber("1234")
                .accountNumber("458714256")
                .cardNumber("2047875414359547")
                .cvc("989")
                .cardHolder("JOANA DARK")
                .expirationDate(YearMonth.of(2031, 4))
                .userId(bankAccount.getId().toHexString())
                .build();

        bankAccountDetailsRequestDTO = BankAccountDetailsRequestDTO.builder()
                .branchNumber("1234")
                .accountNumber("458714256")
                .cardNumber("2047875414359547")
                .cvc("989")
                .cardHolder("JOANA DARK")
                .expirationDate(YearMonth.of(2031, 4))
                .userId(bankAccount.getId().toHexString())
                .build();

        bankAccountRequestDTO = BankAccountRequestDtoFixture.build(
                "Joana Dark",
                LocalDate.of(1990, 5, 20),
                "joana.dark@hotmail.com",
                "+55(98)87669548",
                "05564512300",
                "123456"
        );

        bankAccountResponseDTO = BankAccountResponseDtoFixture.build(
                bankAccount.getId().toHexString(),
                "Joana Dark",
                LocalDate.of(1990, 5, 20),
                "joana.dark@hotmail.com",
                "+55(98)87669548",
                "05564512300"
        );
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

        assertThat(entity)
                .usingRecursiveComparison()
                .ignoringFields("id", "bankAccountDetailsEntities")
                .isEqualTo(bankAccount);
    }

    @Test
    void mustMapBankAccountDetailsToBankAccountDetailsResponseDto(){

        BankAccountDetailsResponseDTO dto = bankAccountMapperConverter
                .toBankAccountDetailsResponseDTO(bankAccountDetails);

        assertEquals(bankAccountDetailsResponseDTO, dto);

    }

    @Test
    void mustMapBankAccountDetailsRequestDtoToBankAccountDetails() {
        BankAccountDetails entity = bankAccountMapperConverter.toBankAccountDetails(bankAccountDetailsRequestDTO);

        assertThat(entity)
                .usingRecursiveComparison()
                .ignoringFields("id", "user")
                .isEqualTo(bankAccountDetails);
    }

    @Test
    void mustMapListBankDetailsRequestDtoToListBankAccountDetailsList(){

        List<BankAccountDetails> bankAccountDetailsList =
                bankAccountMapperConverter.toBankAccountDetailsList(List.of(bankAccountDetailsRequestDTO));

        List<BankAccountDetails> expected = List.of(bankAccountDetails);


        assertThat(bankAccountDetailsList)
                .satisfiesExactly(
                        actual -> assertThat(actual)
                                .usingRecursiveComparison()
                                .ignoringFields("id", "user")
                                .isEqualTo(expected.get(0))
                );

    }

    @Test
    void mustMapListBankAccountToBankAccountListResponseDto(){

        List<BankAccountResponseDTO> bankAccountResponseDTOList =
                bankAccountMapperConverter.toBankAccountResponseDTOList(List.of(bankAccount));

        List<BankAccountResponseDTO> expected = List.of(bankAccountResponseDTO);

        assertThat(bankAccountResponseDTOList)
                .satisfiesExactly(actual -> assertThat(actual)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "user")
                        .isEqualTo(expected.get(0)));
    }

    @Test
    void mustMapBankAccountDetailsListToBankAccountDetailsListResponseDto(){

        List<BankAccountDetailsResponseDTO> bankAccountDetailsResponseDTOList =
                bankAccountMapperConverter.toBankAccountDetailsResponseDTOList(List.of(bankAccountDetails));

        List<BankAccountDetailsResponseDTO> expected = List.of(bankAccountDetailsResponseDTO);

        assertThat(bankAccountDetailsResponseDTOList)
                .satisfiesExactly(actual -> assertThat(actual)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "user")
                        .isEqualTo(expected.get(0)));
    }

    @Test
    void mustUpdateBankAccountByBankAccountRequestDto() {

        bankAccountUpdateMapperConverter.updateBankAccount(bankAccountRequestDTO, bankAccount);

        BankAccount expected = BankAccount.builder()
                .id(bankAccount.getId())
                .name(bankAccountRequestDTO.getName())
                .email(bankAccountRequestDTO.getEmail())
                .password(bankAccountRequestDTO.getPassword())
                .cpf(bankAccountRequestDTO.getCpf())
                .phoneNumber(bankAccountRequestDTO.getPhoneNumber())
                .birthDate(bankAccountRequestDTO.getBirthDate())
                .bankAccountDetailsEntities(bankAccount.getBankAccountDetailsEntities())
                .build();

        assertThat(bankAccount)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

}
