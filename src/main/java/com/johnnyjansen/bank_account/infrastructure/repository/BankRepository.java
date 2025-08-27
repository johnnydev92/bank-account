package com.johnnyjansen.bank_account.infrastructure.repository;

import com.johnnyjansen.bank_account.infrastructure.entities.BankAccountEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankRepository extends JpaRepository<BankAccountEntity, Long> {

    boolean existsByCPF(String cpf);

    Optional<BankAccountEntity> findByCPF(String cpf);

    @Transactional
    void deleteByCPF(String cpf);



}
