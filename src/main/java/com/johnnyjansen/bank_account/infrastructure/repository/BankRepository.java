package com.johnnyjansen.bank_account.infrastructure.repository;

import com.johnnyjansen.bank_account.infrastructure.entities.BankAccount;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankRepository extends JpaRepository<BankAccount, Long> {

    boolean existsByCPF(String cpf);

    Optional<BankAccount> findByEmail(String email);

    Optional<BankAccount> findByCpf(String cpf);

    @Transactional
    void deleteByCPF(String cpf);



}
