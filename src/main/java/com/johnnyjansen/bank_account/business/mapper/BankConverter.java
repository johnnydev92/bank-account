package com.johnnyjansen.bank_account.business.mapper;


import com.johnnyjansen.bank_account.business.dtos.in.BankAccountDetailsRequestDTO;
import com.johnnyjansen.bank_account.business.dtos.in.BankAccountRequestDTO;
import com.johnnyjansen.bank_account.business.dtos.out.BankAccountDetailsResponseDTO;
import com.johnnyjansen.bank_account.business.dtos.out.BankAccountResponseDTO;
import com.johnnyjansen.bank_account.infrastructure.entities.BankAccountDetails;
import com.johnnyjansen.bank_account.infrastructure.entities.BankAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BankConverter {


        @Mapping(source = "user.id", target = "userId")
        BankAccountDetailsResponseDTO toBankAccountDetailsResponseDTO(BankAccountDetails entity);

        List<BankAccountDetailsResponseDTO> toBankAccountDetailsResponseDTOList(List<BankAccountDetails> entities);

        @Mapping(source = "userId", target = "user")
        BankAccountDetails toBankAccountDetails(BankAccountDetailsRequestDTO dto);

        List<BankAccountDetails> toBankAccountDetailsList(List<BankAccountDetailsRequestDTO> dtos);

        BankAccountResponseDTO toBankAccountResponseDTO(BankAccount entity);

        List<BankAccountResponseDTO> toBankAccountResponseDTOList(List<BankAccount> entities);

        @Mapping(target = "password", ignore = true)
        @Mapping(target = "bankAccountDetailsEntities", ignore = true)
        BankAccount toBankAccount(BankAccountRequestDTO dto);

        BankAccountRequestDTO toBankAccountRequestDTO(BankAccount entity);



        default BankAccount map(Long userId) {
            if (userId == null) {
                return null;
                 }
        return BankAccount.builder().id(userId).build();
    }



}
