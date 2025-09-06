package com.johnnyjansen.bank_account.infrastructure.repository;



import com.johnnyjansen.bank_account.infrastructure.entities.BankAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankRepository extends MongoRepository<BankAccount, String> {

    boolean existsByCpf(String cpf);

    Optional<BankAccount> findByEmail(String email);

    Optional<BankAccount> findByCpf(String cpf);





}
