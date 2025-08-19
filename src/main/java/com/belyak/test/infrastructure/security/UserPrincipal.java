package com.belyak.test.infrastructure.security;

import com.belyak.test.domain.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserPrincipal implements UserDetails {

    @EqualsAndHashCode.Include
    private final UUID id;

    private final String email;
    private final String username;
    private final String password;
    private final boolean active;

    private final Collection<? extends GrantedAuthority> authorities;

    public static UserPrincipal fromDomain(User user) {
        return new UserPrincipal(
                user.getId(),
                user.getEmail().value(),
                user.getUsername().value(),
                user.getPassword().value(),
                user.isActive(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    public String getActualUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.active;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.active;
    }

    @Override
    public boolean isEnabled() {
        return this.active;
    }
}
