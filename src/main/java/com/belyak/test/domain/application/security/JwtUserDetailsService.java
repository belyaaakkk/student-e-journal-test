package com.belyak.test.domain.application.security;

import com.belyak.test.domain.user.model.User;
import com.belyak.test.domain.common.UserRepository;
import com.belyak.test.infrastructure.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // identifier может быть как email (при логине), так и username (при валидации JWT)

        // Сначала пробуем найти по email
        Optional<User> userByEmail = userRepository.findByEmail(identifier);
        if (userByEmail.isPresent()) {
            return UserPrincipal.fromDomain(userByEmail.get());
        }

        // Если не найден по email, пробуем по username
        return userRepository.findByUsername(identifier)
                .map(UserPrincipal::fromDomain)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + identifier));
    }
}
