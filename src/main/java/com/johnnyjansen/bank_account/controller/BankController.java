package com.johnnyjansen.bank_account.controller;

import com.johnnyjansen.bank_account.business.dtos.in.BankAccountRequestDTO;
import com.johnnyjansen.bank_account.business.dtos.out.BankAccountDetailsResponseDTO;
import com.johnnyjansen.bank_account.business.dtos.out.BankAccountResponseDTO;
import com.johnnyjansen.bank_account.business.service.BankService;
import com.johnnyjansen.bank_account.infrastructure.security.JwtUtil;
import com.johnnyjansen.bank_account.infrastructure.security.SecurityConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bankAccount")
@RequiredArgsConstructor
@Tag(name = "bankUser", description = "search, create, update and delete bank user accounts")
@SecurityRequirement(name = SecurityConfig.SECURITY_SCHEME)
public class BankController {

    private final BankService bankService;
    private final JwtUtil jwtUtil;


    @GetMapping
    @Operation(summary = "Search user account", description = "Seek user account by email")
    @ApiResponse(responseCode = "200", description = "User founded successfully")
    @ApiResponse(responseCode = "404", description = "User cannot be found. Try later.")
    @ApiResponse(responseCode = "409", description = "Invalid email or null. Try again.")
    @ApiResponse(responseCode = "500", description = "Server error!")
    public ResponseEntity<BankAccountResponseDTO> searchUserByEmail(@RequestParam("email") String email) {
        if (email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

        BankAccountResponseDTO dto = bankService.searchUserByEmail(email);
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/userAccount/details")
    @Operation(summary = "Search user details", description = "Search user details by token")
    @ApiResponse(responseCode = "200", description = "User details founded successfully")
    @ApiResponse(responseCode = "404", description = "User cannot be found. Try later.")
    @ApiResponse(responseCode = "409", description = "Invalid email or null. Try again.")
    @ApiResponse(responseCode = "500", description = "Server error!")
    public ResponseEntity<BankAccountDetailsResponseDTO> searchUserDetailsByToken
            (@RequestHeader("Authorization") String token) {

        BankAccountDetailsResponseDTO responseDTO =
                bankService.searchUserDetailsByToken(token);

        return ResponseEntity.ok(responseDTO);


    }

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Creates a new user bank account")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @ApiResponse(responseCode = "409", description = "CPF already registered")
    @ApiResponse(responseCode = "500", description = "Server error!")
    public ResponseEntity<BankAccountResponseDTO> registerUser(
            @RequestBody BankAccountRequestDTO requestDTO) {
        try {
            BankAccountResponseDTO responseDTO = bankService.saveUser(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (ErrorResponseException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login user account", description = "Login user by email and password")
    @ApiResponse(responseCode = "200", description = "Login was success")
    @ApiResponse(responseCode = "409", description = "Wrong email or password or user do not exists! Try again.")
    @ApiResponse(responseCode = "500", description = "Server error!")
    public ResponseEntity<String> loginUser(@RequestBody BankAccountRequestDTO bankAccountRequestDTO){

        String token = bankService.loginUser(bankAccountRequestDTO);
        return ResponseEntity.ok(token);

    }

    @PatchMapping("/updateBankAccount")
    @Operation(summary = "Update user account", description = "Update user account")
    @ApiResponse(responseCode = "200", description = "Login was success")
    @ApiResponse(responseCode = "409", description = "Wrong email or password or user do not exists! Try again.")
    @ApiResponse(responseCode = "500", description = "Server error!")
    public ResponseEntity<BankAccountResponseDTO> updateUserAccount(@RequestBody BankAccountRequestDTO dto,
            @RequestHeader("Authorization") String token){

        BankAccountResponseDTO updatedUser = bankService.updateBankAccount(dto, token);

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{cpf}")
    @Operation(summary = "Delete user account", description = "Delete user account by token and cpf")
    @ApiResponse(responseCode = "200", description = "User account was deleted successfully")
    @ApiResponse(responseCode = "409", description = "Invalid token or wrong cpf.")
    @ApiResponse(responseCode = "500", description = "Server error!")
    public ResponseEntity<Void> deleteUserAccountByTokenCpf
            (@RequestHeader("Authorization") String token){

            bankService.deleteAccountByToken(token);
            return ResponseEntity.accepted().build();
    }




}
