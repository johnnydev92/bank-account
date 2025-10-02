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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
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
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());

        verify(bankService).searchUserByEmail(bankAccountRequestDTO.getEmail());
        verifyNoMoreInteractions(bankService);
    }

    @Test
    void mustSearchBankDetailsByToken() throws Exception{

        String token = "Bearer fake-token";

        when(bankService.searchUserDetailsByToken(token))
                .thenReturn(bankAccountDetailsResponseDTO);

        mockMvc.perform(get(url + "/userAccount/details")
                        .header("Authorization", token)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bankService).searchUserDetailsByToken(token);
        verifyNoMoreInteractions(bankService);

    }

    @Test
    void mustRegisterBankAccountSuccessfully() throws Exception {
        when(bankService.saveUser(any(BankAccountRequestDTO.class)))
                .thenReturn(bankAccountResponseDTO);

        mockMvc.perform(post(url + "/register")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        verify(bankService).saveUser(any(BankAccountRequestDTO.class));
        verifyNoMoreInteractions(bankService);
    }

    @Test
    void mustLoginBankAccountUserSuccessfully() throws Exception {

        String token = "Bearer fake-token";

        when(bankService.loginUser
                (bankAccountRequestDTO)).thenReturn(token);

        mockMvc.perform(post(url + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(bankService).loginUser(bankAccountRequestDTO);
        verifyNoMoreInteractions(bankService);
    }

    @Test
    void mustUpdateBankAccountSuccessfully() throws Exception {

        String token = "Bearer fake-token";

        when(bankService.updateBankAccount(bankAccountRequestDTO, token))
                .thenReturn(bankAccountResponseDTO);

        String expectedJson = objectMapper.writeValueAsString(bankAccountResponseDTO);

        mockMvc.perform(patch("/bankAccount/updateBankAccount")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

        verify(bankService).updateBankAccount(bankAccountRequestDTO, token);
        verifyNoMoreInteractions(bankService);



    }

    @Test
    void mustDeleteBankAccountUserByToken() throws Exception{

        String token = "Bearer fake-token";

        doNothing().when(bankService).deleteAccountByToken(token);

        mockMvc.perform(delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(json))
                .andExpect(status().isAccepted());

        verify(bankService).deleteAccountByToken(token);
        verifyNoMoreInteractions(bankService);

    }
}
