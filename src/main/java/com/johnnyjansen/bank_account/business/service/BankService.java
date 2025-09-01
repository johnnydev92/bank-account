package com.johnnyjansen.bank_account.business.service;


import com.johnnyjansen.bank_account.business.dtos.in.BankAccountDetailsRequestDTO;
import com.johnnyjansen.bank_account.business.dtos.in.BankAccountRequestDTO;
import com.johnnyjansen.bank_account.business.dtos.out.BankAccountDetailsResponseDTO;
import com.johnnyjansen.bank_account.business.dtos.out.BankAccountResponseDTO;
import com.johnnyjansen.bank_account.business.mapper.BankAccountUpdateConverter;
import com.johnnyjansen.bank_account.business.mapper.BankConverter;
import com.johnnyjansen.bank_account.infrastructure.entities.BankAccountDetails;
import com.johnnyjansen.bank_account.infrastructure.entities.BankAccount;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepository;
    private final BankDetailsRepository bankDetailsRepository;
    private final JwtUtil jwtUtil;
    private BankConverter bankConverter;
    private BankAccountUpdateConverter bankAccountUpdateConverter;
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

        BankAccount entity = bankConverter.toBankAccount(entityRequestDTO);

        entity.setPassword(encryptedPassword);

        BankAccount savedEntity = bankRepository.save(entity);

        return bankConverter.toBankAccountResponseDTO(savedEntity);
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
        return bankRepository.existsByCPF(cpf);
    }

    public String loginUser(BankAccount bankAccountEntity) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            bankAccountEntity.getEmail(),
                            bankAccountEntity.getPassword()
                    )
            );

            return "Bearer " + jwtUtil.generateToken(authentication.getName());

        } catch (Exception ex) {
            throw new ConflictException("Invalid email or password");
        }
    }

    public BankAccountResponseDTO searchUserByEmail(String email){
        try {
            return bankConverter.toBankAccountResponseDTO(
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
            (String token, BankAccountDetailsRequestDTO detailsRequestDTO) {

        String cleanToken = token.substring(7);

        String email = jwtUtil.extractEmail(cleanToken);

        BankAccount bankAccount = bankRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for token"));

        BankAccountDetails user = bankDetailsRepository.findByUser(bankAccount)
                .orElseThrow(() -> new ResourceNotFoundException("Details not found for user"));

        return bankConverter.toBankAccountDetailsResponseDTO(user);

    }

    public BankAccountResponseDTO updateBankAccount(BankAccountRequestDTO bankAccountRequestDTO, String id){

        BankAccount bankAccount = bankRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new ResourceNotFoundException("Error. User cannot be located: " + id));

        bankAccountUpdateConverter.updateBankAccount(bankAccountRequestDTO, bankAccount);

        BankAccount savedAccount = bankRepository.save(bankAccount);

        return bankConverter.toBankAccountResponseDTO(savedAccount);

    }

    public void deleteAccountByCpf(String cpf, String token){

        String cleanToken = token.substring(7);

        String cpfExtracted = jwtUtil.extractCpf(cleanToken);

        if(!jwtUtil.validateToken(cleanToken, cpf)){
            throw new ConflictException("Token is invalid! Try again.");
        }

        boolean cpfExists = bankRepository.existsByCPF(cpf);
        if(!cpfExists){
            throw new ResourceNotFoundException("User cannot be found! Try later.");
        }

        bankRepository.deleteByCPF(cpf);

    }


}
