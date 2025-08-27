package com.johnnyjansen.bank_account.business.service;


import com.johnnyjansen.bank_account.business.dtos.in.BankAccountDetailsRequestDTO;
import com.johnnyjansen.bank_account.business.dtos.in.BankAccountEntityRequestDTO;
import com.johnnyjansen.bank_account.business.dtos.out.BankAccountDetailsResponseDTO;
import com.johnnyjansen.bank_account.business.dtos.out.BankAccountEntityResponseDTO;
import com.johnnyjansen.bank_account.business.mapper.BankConverter;
import com.johnnyjansen.bank_account.infrastructure.entities.BankAccountDetailsEntity;
import com.johnnyjansen.bank_account.infrastructure.entities.BankAccountEntity;
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
    private BankConverter bankConverter;
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

    public BankAccountEntityResponseDTO saveUser
            (BankAccountEntityRequestDTO entityRequestDTO) throws Exception {

        doesCPfExists(entityRequestDTO.getCpf());

        String encryptedPassword = encryptor.encrypt(entityRequestDTO.getPassword());

        BankAccountEntity entity = bankConverter.toBankAccountEntity(entityRequestDTO);

        entity.setPassword(encryptedPassword);

        BankAccountEntity savedEntity = bankRepository.save(entity);

        return bankConverter.toBankAccountEntityResponseDTO(savedEntity);
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

    public String loginUser(BankAccountEntity bankAccountEntity) {
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

    public BankAccountEntityResponseDTO searchUserByCpf(String cpf){
        try {
            return bankConverter.toBankAccountEntityResponseDTO(
                    bankRepository.findByCPF(cpf)
                            .orElseThrow(()
                                    -> new ResourceNotFoundException("Invalid cpf or do not exists." + cpf)
                            )
            );
        }catch (ResourceNotFoundException e){
            throw new ResourceNotFoundException("Invalid cpf or do not exists." + cpf);
        }
    }

    public BankAccountDetailsResponseDTO searchUserDetailsByToken (String token, BankAccountDetailsRequestDTO DetailsRequestDTO){

        String cleanToken = token.substring(7);




}
