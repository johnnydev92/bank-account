package com.johnnyjansen.bank_account.infrastructure.repository;


import com.johnnyjansen.bank_account.infrastructure.entities.BankAccount;
import com.johnnyjansen.bank_account.infrastructure.entities.BankAccountDetails;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BankDetailsRepository extends MongoRepository<BankAccountDetails, String> {

    Optional<BankAccountDetails> findByUser(BankAccount user);
}
