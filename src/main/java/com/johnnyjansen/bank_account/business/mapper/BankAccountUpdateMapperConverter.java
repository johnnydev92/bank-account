package com.johnnyjansen.bank_account.business.mapper;

import com.johnnyjansen.bank_account.business.dtos.in.BankAccountRequestDTO;
import com.johnnyjansen.bank_account.infrastructure.entities.BankAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BankAccountUpdateMapperConverter {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bankAccountDetailsEntities", ignore = true)
    void updateBankAccount(BankAccountRequestDTO dto, @MappingTarget BankAccount bankAccount);

}
