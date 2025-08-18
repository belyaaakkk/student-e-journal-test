package com.belyak.test.infrastructure.security;

import com.belyak.test.application.user.adapter.UserRepositoryAdapter;
import com.belyak.test.domain.user.model.User;
import com.belyak.test.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(UserPrincipal::fromDomain)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
