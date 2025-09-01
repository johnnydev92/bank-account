package com.johnnyjansen.bank_account.infrastructure.security;


import com.johnnyjansen.bank_account.infrastructure.entities.BankAccount;
import com.johnnyjansen.bank_account.infrastructure.repository.BankRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    // Repository to access user data from the database
    private final BankRepository bankRepository;

    public UserDetailsServiceImpl(BankRepository bankRepository1) {
        this.bankRepository = bankRepository1;
    }

    // Implementation of the method to load user details by CPF (used as username)
    @Override
    public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {
        // Search the user in the database by CPF
        BankAccount user = bankRepository.findByEmail(cpf)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Account cannot be found or does not exist: " + cpf));

        // Create and return a UserDetails object based on the found user
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail()) // Set the username as the user's email
                .password("") // Set the password (you should ideally load the real hashed password)
                .build(); // Build the UserDetails object

    }
}
