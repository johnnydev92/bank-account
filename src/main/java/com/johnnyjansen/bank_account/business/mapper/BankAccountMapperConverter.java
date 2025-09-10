package com.johnnyjansen.bank_account.business.mapper;


import com.johnnyjansen.bank_account.business.dtos.in.BankAccountDetailsRequestDTO;
import com.johnnyjansen.bank_account.business.dtos.in.BankAccountRequestDTO;
import com.johnnyjansen.bank_account.business.dtos.out.BankAccountDetailsResponseDTO;
import com.johnnyjansen.bank_account.business.dtos.out.BankAccountResponseDTO;
import com.johnnyjansen.bank_account.infrastructure.entities.BankAccountDetails;
import com.johnnyjansen.bank_account.infrastructure.entities.BankAccount;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BankAccountMapperConverter {

    @Mapping(source = "id", target = "id", qualifiedByName = "objectIdToString")
    @Mapping(source = "user.id", target = "userId", qualifiedByName = "objectIdToString")
    BankAccountDetailsResponseDTO toBankAccountDetailsResponseDTO(BankAccountDetails entity);

    List<BankAccountDetailsResponseDTO> toBankAccountDetailsResponseDTOList(List<BankAccountDetails> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "userId", target = "user", qualifiedByName = "stringToBankAccount")
    BankAccountDetails toBankAccountDetails(BankAccountDetailsRequestDTO dto);

    List<BankAccountDetails> toBankAccountDetailsList(List<BankAccountDetailsRequestDTO> dtos);

    @Mapping(source = "id", target = "id", qualifiedByName = "objectIdToString")
    BankAccountResponseDTO toBankAccountResponseDTO(BankAccount entity);

    List<BankAccountResponseDTO> toBankAccountResponseDTOList(List<BankAccount> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bankAccountDetailsEntities", ignore = true)
    BankAccount toBankAccount(BankAccountRequestDTO dto);

    BankAccountRequestDTO toBankAccountRequestDTO(BankAccount entity);

    @Named("stringToBankAccount")
    default BankAccount stringToBankAccount(String id) {
        return id != null ? BankAccount.builder().id(new ObjectId(id)).build() : null;
    }

    @Named("objectIdToString")
    default String objectIdToString(ObjectId id) {
        return id != null ? id.toHexString() : null;
    }


}
