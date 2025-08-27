package com.johnnyjansen.bank_account.business.mapper;


import com.johnnyjansen.bank_account.business.dtos.in.BankAccountDetailsRequestDTO;
import com.johnnyjansen.bank_account.business.dtos.in.BankAccountEntityRequestDTO;
import com.johnnyjansen.bank_account.business.dtos.out.BankAccountDetailsResponseDTO;
import com.johnnyjansen.bank_account.business.dtos.out.BankAccountEntityResponseDTO;
import com.johnnyjansen.bank_account.infrastructure.entities.BankAccountDetailsEntity;
import com.johnnyjansen.bank_account.infrastructure.entities.BankAccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BankConverter {


        @Mapping(source = "user.id", target = "userId")
        BankAccountDetailsResponseDTO toBankAccountDetailsResponseDTO(BankAccountDetailsEntity entity);

        List<BankAccountDetailsResponseDTO> toBankAccountDetailsResponseDTOList(List<BankAccountDetailsEntity> entities);

        @Mapping(source = "userId", target = "user")
        BankAccountDetailsEntity toBankAccountDetailsEntity(BankAccountDetailsRequestDTO dto);

        List<BankAccountDetailsEntity> toBankAccountDetailsEntityList(List<BankAccountDetailsRequestDTO> dtos);

        BankAccountEntityResponseDTO toBankAccountEntityResponseDTO(BankAccountEntity entity);

        List<BankAccountEntityResponseDTO> toBankAccountEntityResponseDTOList(List<BankAccountEntity> entities);

        @Mapping(target = "password", ignore = true)
        @Mapping(target = "bankAccountDetailsEntities", ignore = true)
        BankAccountEntity toBankAccountEntity(BankAccountEntityRequestDTO dto);

        BankAccountEntityRequestDTO toBankAccountEntityRequestDTO(BankAccountEntity entity);



        default BankAccountEntity map(Long userId) {
            if (userId == null) {
                return null;
                 }
        return BankAccountEntity.builder().id(userId).build();
    }



}
