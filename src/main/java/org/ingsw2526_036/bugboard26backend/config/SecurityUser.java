package org.ingsw2526_036.bugboard26backend.config;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.ingsw2526_036.bugboard26backend.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adapter per Spring Security UserDetails.
 * Separa le responsabilita di autenticazione del framework
 * dai campi del dominio dell'entita User.
 */
@RequiredArgsConstructor
@Getter
public class SecurityUser implements UserDetails {

    private final User user;

    @Override
    public @NonNull String getUsername() {
        return user.getEmail(); // Email usata come identificativo di login
    }

    @Override
    public @NonNull String getPassword() {
        return user.getPassword();
    }

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
