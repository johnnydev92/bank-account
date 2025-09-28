package com.johnnyjansen.bank_account.api.tests.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.johnnyjansen.bank_account.api.tests.request.BankAccountRequestDtoFixture;
import com.johnnyjansen.bank_account.api.tests.response.BankAccountResponseDtoFixture;
import com.johnnyjansen.bank_account.business.dtos.in.BankAccountDetailsRequestDTO;
import com.johnnyjansen.bank_account.business.dtos.in.BankAccountRequestDTO;
import com.johnnyjansen.bank_account.business.dtos.out.BankAccountDetailsResponseDTO;
import com.johnnyjansen.bank_account.business.dtos.out.BankAccountResponseDTO;
import com.johnnyjansen.bank_account.business.service.BankService;
import com.johnnyjansen.bank_account.controller.BankController;
import com.johnnyjansen.bank_account.controller.GlobalExceptionHandler;
import com.johnnyjansen.bank_account.infrastructure.entities.BankAccount;
import com.johnnyjansen.bank_account.infrastructure.entities.BankAccountDetails;
import com.johnnyjansen.bank_account.infrastructure.security.JwtUtil;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ControllerTest {

    @InjectMocks
    BankController bankController;

    @Mock
    BankService bankService;

    @Mock
    JwtUtil jwtUtil;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private String url;

    private String json;

    BankAccountResponseDTO bankAccountResponseDTO;

    BankAccountRequestDTO bankAccountRequestDTO;

    BankAccountDetailsResponseDTO bankAccountDetailsResponseDTO;

    BankAccountDetailsRequestDTO bankAccountDetailsRequestDTO;

    BankAccount bankAccount;

    BankAccountDetails bankAccountDetails;

    @BeforeEach
    void setup() throws JsonProcessingException {
        mockMvc = MockMvcBuilders.standaloneSetup(bankController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .alwaysDo(print())
                .build();

        url = "/bankAccount";

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

        json = objectMapper.writeValueAsString
                (bankAccountRequestDTO);
    }

    @Test
    void mustSearchBankAccountByEmail() throws Exception{

        when(bankService.searchUserByEmail(bankAccountRequestDTO.getEmail()))
                .thenReturn(bankAccountResponseDTO);

        mockMvc.perform(get(url)
                        .param("email", bankAccountRequestDTO.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());

        verify(bankService).searchUserByEmail(bankAccountRequestDTO.getEmail());
        verifyNoMoreInteractions(bankService);
    }

}
