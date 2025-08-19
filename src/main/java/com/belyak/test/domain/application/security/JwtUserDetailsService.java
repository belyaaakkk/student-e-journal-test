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

        Optional<User> userByEmail = userRepository.findByEmail(identifier);
        return userByEmail.map(UserPrincipal::fromDomain)
                .orElseGet(() -> userRepository.findByUsername(identifier)
                .map(UserPrincipal::fromDomain)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + identifier)));
    }
}
