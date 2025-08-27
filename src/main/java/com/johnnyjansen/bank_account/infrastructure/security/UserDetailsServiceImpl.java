package com.johnnyjansen.bank_account.infrastructure.security;


import com.johnnyjansen.bank_account.infrastructure.entities.BankAccountEntity;
import com.johnnyjansen.bank_account.infrastructure.repository.BankRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    // Repositório para acessar dados de usuário no banco de dados

    private final BankRepository bankRepository;

    public UserDetailsServiceImpl(BankRepository bankRepository1) {
        this.bankRepository = bankRepository1;
    }

    // Implementação do método para carregar detalhes do usuário pelo crm
    @Override
    public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {
        // Busca o usuário no banco de dados pelo e-mail
        BankAccountEntity user = bankRepository.findByCPF(cpf)
                .orElseThrow(() -> new UsernameNotFoundException
                        ("Account cannot be found or does not exist: " + cpf));

        // Cria e retorna um objeto UserDetails com base no usuário encontrado
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail()) // Define o nome de usuário como o e-mail
                .password("") // Define a senha do usuário
                .build(); // Constrói o objeto UserDetails
    }
}
