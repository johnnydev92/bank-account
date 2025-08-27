package com.johnnyjansen.bank_account.infrastructure.entities;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bank_account")
@Builder
public class BankAccountEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty(required = true)
    @Column(name = "name", updatable = true, unique = true)
    private String name;

    @JsonProperty(required = true)
    @Column(name = "birth_date", updatable = true)
    private LocalDate birthDate;

    @JsonProperty(required = true)
    @Column(name = "email", updatable = true, unique = true)
    private String email;

    @JsonProperty(required = true)
    @Column(name = "password", updatable = true, unique = true)
    private String password;

    @JsonProperty(required = true)
    @Column(name = "phone_number", updatable = true, unique = true)
    private String phoneNumber;

    @JsonProperty(required = true)
    @Column(name = "CPF", updatable = true, unique = true)
    private String cpf;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BankAccountDetailsEntity> bankAccountDetailsEntities;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
