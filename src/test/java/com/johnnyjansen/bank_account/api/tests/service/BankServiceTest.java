package com.johnnyjansen.bank_account.api.tests.service;


import com.johnnyjansen.bank_account.api.tests.request.BankAccountRequestDtoFixture;
import com.johnnyjansen.bank_account.api.tests.response.BankAccountResponseDtoFixture;
import com.johnnyjansen.bank_account.business.dtos.in.BankAccountDetailsRequestDTO;
import com.johnnyjansen.bank_account.business.dtos.in.BankAccountRequestDTO;
import com.johnnyjansen.bank_account.business.dtos.out.BankAccountDetailsResponseDTO;
import com.johnnyjansen.bank_account.business.dtos.out.BankAccountResponseDTO;
import com.johnnyjansen.bank_account.business.mapper.BankAccountMapperConverter;
import com.johnnyjansen.bank_account.business.mapper.BankAccountUpdateMapperConverter;
import com.johnnyjansen.bank_account.business.service.BankService;
import com.johnnyjansen.bank_account.infrastructure.entities.BankAccount;
import com.johnnyjansen.bank_account.infrastructure.entities.BankAccountDetails;
import com.johnnyjansen.bank_account.infrastructure.exceptions.ConflictException;
import com.johnnyjansen.bank_account.infrastructure.exceptions.ResourceNotFoundException;
import com.johnnyjansen.bank_account.infrastructure.repository.BankDetailsRepository;
import com.johnnyjansen.bank_account.infrastructure.repository.BankRepository;
import com.johnnyjansen.bank_account.infrastructure.security.AES256Encryptor;
import com.johnnyjansen.bank_account.infrastructure.security.JwtUtil;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BankServiceTest {

    @InjectMocks
    private BankService bankService;

    @Mock
    private BankRepository bankRepository;

    @Mock
    private BankDetailsRepository bankDetailsRepository;

    @Mock
    private AES256Encryptor encryptor;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private BankAccountMapperConverter bankAccountMapperConverter;

    @Mock
    private BankAccountUpdateMapperConverter bankAccountUpdateMapperConverter;

    @Mock
    private AuthenticationManager authenticationManager;

    BankAccount bankAccount;

    BankAccountDetails bankAccountDetails;

    BankAccountDetailsResponseDTO bankAccountDetailsResponseDTO;

    BankAccountDetailsRequestDTO bankAccountDetailsRequestDTO;

    BankAccountResponseDTO bankAccountResponseDTO;

    BankAccountRequestDTO bankAccountRequestDTO;


    @BeforeEach
    public void setup() {

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
    void mustSaveUserSuccessfully() throws Exception {

        when(bankRepository.existsByCpf("05564512300")).thenReturn(false);
        when(encryptor.encrypt("123456")).thenReturn("encryptedPassword");
        when(bankAccountMapperConverter.toBankAccount(bankAccountRequestDTO)).thenReturn(bankAccount);
        when(bankRepository.save(bankAccount)).thenReturn(bankAccount);
        when(bankAccountMapperConverter.toBankAccountResponseDTO(bankAccount)).thenReturn(bankAccountResponseDTO);


        BankAccountResponseDTO response = bankService.saveUser(bankAccountRequestDTO);


        assertNotNull(response);
        assertEquals("05564512300", response.getCpf());
        assertEquals("Joana Dark", response.getName());


        verify(bankRepository).existsByCpf("05564512300");
        verify(encryptor).encrypt("123456");
        verify(bankAccountMapperConverter).toBankAccount(bankAccountRequestDTO);
        verify(bankRepository).save(bankAccount);
        verify(bankAccountMapperConverter).toBankAccountResponseDTO(bankAccount);
    }

    @Test
    void mustThrowConflictExceptionWhenCpfExists() {

        when(bankRepository.existsByCpf("05564512300")).thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            bankService.saveUser(bankAccountRequestDTO);
        });

        assertTrue(exception.getMessage().contains("CPF already exists"));
    }

    @Test
    void mustLoginUserSuccessfully(){

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(bankAccountRequestDTO.getEmail());

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        bankAccountRequestDTO.getEmail(),
                        bankAccountRequestDTO.getPassword()
                )
        )).thenReturn(authentication);

        String mockedToken = "mocked-jwt-token";
        when(jwtUtil.generateToken(bankAccountRequestDTO.getEmail())).thenReturn(mockedToken);

        String result = bankService.loginUser(bankAccountRequestDTO);

        assertEquals("Bearer " + mockedToken, result);
    }

    @Test
    void mustThrowConflictExceptionWhenLoginFails() {

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        bankAccountRequestDTO.getEmail(),
                        bankAccountRequestDTO.getPassword()
                )
        )).thenThrow(new BadCredentialsException("Bad credentials"));

        ConflictException exception =
                assertThrows(ConflictException.class, () -> {
            bankService.loginUser(bankAccountRequestDTO);
        });

        assertEquals("Invalid email or password",
                exception.getMessage());
    }

    @Test
    void mustSearchUserByEmail(){

        when(bankRepository.findByEmail(bankAccount.getEmail())).
                thenReturn(Optional.of(bankAccount));

        when(bankAccountMapperConverter.
                toBankAccountResponseDTO(bankAccount)).
                thenReturn(bankAccountResponseDTO);

        BankAccountResponseDTO result = bankService.searchUserByEmail(bankAccount.getEmail());

        assertNotNull(result);
        assertEquals(bankAccountResponseDTO.getEmail(), result.getEmail());
        assertEquals(bankAccountResponseDTO.getName(), result.getName());
    }

    @Test
    void mustThrowResourceNotFoundExceptionWhenEmailNotExists() {

        String emailNotFound = "notfound@example.com";

        when(bankRepository.findByEmail(emailNotFound))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            bankService.searchUserByEmail(emailNotFound);
        });

        assertEquals("Invalid email or do not exists." + emailNotFound, exception.getMessage());
    }


}


