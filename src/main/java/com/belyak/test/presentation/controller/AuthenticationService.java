package com.belyak.test.presentation.controller;

import com.belyak.test.domain.user.model.User;
import com.belyak.test.domain.user.repository.UserRepository;
import com.belyak.test.domain.user.value.Email;
import com.belyak.test.domain.user.value.Password;
import com.belyak.test.domain.user.value.Username;
import com.belyak.test.infrastructure.security.JwtService;
import com.belyak.test.infrastructure.security.UserPrincipal;
import com.belyak.test.presentation.dto.AuthenticationRequest;
import com.belyak.test.presentation.dto.AuthenticationResponse;
import com.belyak.test.presentation.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        User user = User.createNew(
                new Email(request.getEmail()),
                new Username(request.getUsername()),
                new Password(passwordEncoder.encode(request.getPassword()))
        );
        User saved = userRepository.save(user);

        UserPrincipal principal = UserPrincipal.fromDomain(saved);
        String jwtToken = jwtService.generateToken(principal);
        String refreshToken = jwtService.generateRefreshToken(principal);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserPrincipal principal = UserPrincipal.fromDomain(user);

        String jwtToken = jwtService.generateToken(principal);
        String refreshToken = jwtService.generateRefreshToken(principal);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse refreshToken(String refreshToken) {
        final String userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail == null) {
            throw new RuntimeException("Invalid refresh token");
        }

        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var userPrincipal = UserPrincipal.fromDomain(user);

        if (!jwtService.isTokenValid(refreshToken, userPrincipal)) {
            throw new RuntimeException("Invalid refresh token");
        }

        var accessToken = jwtService.generateToken(userPrincipal);
        var newRefreshToken = jwtService.generateRefreshToken(userPrincipal);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
