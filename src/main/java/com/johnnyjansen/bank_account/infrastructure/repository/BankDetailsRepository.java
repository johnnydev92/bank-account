package com.johnnyjansen.bank_account.infrastructure.repository;

import com.johnnyjansen.bank_account.infrastructure.entities.BankAccount;
import com.johnnyjansen.bank_account.infrastructure.entities.BankAccountDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankDetailsRepository extends JpaRepository<BankAccountDetails, Long> {

    Optional<BankAccountDetails> findByUser(BankAccount user);
}
