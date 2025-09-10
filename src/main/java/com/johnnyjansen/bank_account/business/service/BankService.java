package com.johnnyjansen.bank_account.business.service;


import com.johnnyjansen.bank_account.business.dtos.in.BankAccountRequestDTO;
import com.johnnyjansen.bank_account.business.dtos.out.BankAccountDetailsResponseDTO;
import com.johnnyjansen.bank_account.business.dtos.out.BankAccountResponseDTO;
import com.johnnyjansen.bank_account.business.mapper.BankAccountUpdateMapperConverter;
import com.johnnyjansen.bank_account.business.mapper.BankAccountMapperConverter;
import com.johnnyjansen.bank_account.infrastructure.entities.BankAccount;
import com.johnnyjansen.bank_account.infrastructure.entities.BankAccountDetails;
import com.johnnyjansen.bank_account.infrastructure.exceptions.ConflictException;
import com.johnnyjansen.bank_account.infrastructure.exceptions.ResourceNotFoundException;
import com.johnnyjansen.bank_account.infrastructure.repository.BankDetailsRepository;
import com.johnnyjansen.bank_account.infrastructure.repository.BankRepository;
import com.johnnyjansen.bank_account.infrastructure.security.AES256Encryptor;
import com.johnnyjansen.bank_account.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepository;
    private final BankDetailsRepository bankDetailsRepository;
    private final JwtUtil jwtUtil;
    private BankAccountMapperConverter bankAccountMapperConverter;
    private BankAccountUpdateMapperConverter bankAccountUpdateMapperConverter;
    private AuthenticationManager authenticationManager;
    @Autowired
    private AES256Encryptor encryptor;

    public void testEncryption() throws Exception {
        String originalText = "CVC=123";

        String encryptedText = encryptor.encrypt(originalText);
        String decryptedText = encryptor.decrypt(encryptedText);

        System.out.println("Original:" + originalText);
        System.out.println("Encrypted: " + encryptedText);
        System.out.println("Decrypted: " + decryptedText);
    }

    public BankAccountResponseDTO saveUser
            (BankAccountRequestDTO entityRequestDTO) throws Exception {

        doesCPfExists(entityRequestDTO.getCpf());

        String encryptedPassword = encryptor.encrypt(entityRequestDTO.getPassword());

        BankAccount entity = bankAccountMapperConverter.toBankAccount(entityRequestDTO);

        entity.setPassword(encryptedPassword);

        BankAccount savedEntity = bankRepository.save(entity);

        return bankAccountMapperConverter.toBankAccountResponseDTO(savedEntity);
    }

    public void doesCPfExists(String cpf){
        try{
            boolean CpfExists =  checkCpfExists(cpf);
            if(CpfExists) {
                throw new ConflictException("CPF already exists." + cpf);
            }

        }catch (ConflictException e){
            throw new ConflictException("CPF already exists." + e.getCause());
        }
    }

    public boolean checkCpfExists(String cpf){
        return bankRepository.existsByCpf(cpf);
    }

    public String loginUser(BankAccountRequestDTO bankAccountRequestDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            bankAccountRequestDTO.getEmail(),
                            bankAccountRequestDTO.getPassword()
                    )
            );

            return "Bearer " + jwtUtil.generateToken(authentication.getName());

        } catch (Exception ex) {
            throw new ConflictException("Invalid email or password");
        }
    }

    public BankAccountResponseDTO searchUserByEmail(String email){
        try {
            return bankAccountMapperConverter.toBankAccountResponseDTO(
                    bankRepository.findByEmail(email)
                            .orElseThrow(()
                                    -> new ResourceNotFoundException("Invalid email or do not exists." + email)
                            )
            );
        }catch (ResourceNotFoundException e){
            throw new ResourceNotFoundException("Invalid email or do not exists." + email);
        }
    }

    public BankAccountDetailsResponseDTO searchUserDetailsByToken
            (String token) {

        String cleanToken = token.substring(7);

        String email = jwtUtil.extractEmail(cleanToken);

        BankAccount bankAccount = bankRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for token"));

        BankAccountDetails user = bankDetailsRepository.findByUser(bankAccount)
                .orElseThrow(() -> new ResourceNotFoundException("Details not found for user"));

        return bankAccountMapperConverter.toBankAccountDetailsResponseDTO(user);

    }

    public BankAccountResponseDTO updateBankAccount(BankAccountRequestDTO bankAccountRequestDTO, String token){

        String cleanToken = token.substring(7);

        String email = jwtUtil.extractEmail(cleanToken);

        BankAccount bankAccount = bankRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for token or invalid token!"));

        bankAccountUpdateMapperConverter.updateBankAccount(bankAccountRequestDTO, bankAccount);

        BankAccount savedAccount = bankRepository.save(bankAccount);

        return bankAccountMapperConverter.toBankAccountResponseDTO(savedAccount);

    }


    public void deleteAccountByToken(String token){

        String cleanToken = token.substring(7);

        String email = jwtUtil.extractEmail(cleanToken);

        if (!jwtUtil.validateToken(cleanToken, email)) {
            throw new ConflictException("Token is invalid! Try again.");
        }

        BankAccount account = bankRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        bankRepository.delete(account);

    }


}
