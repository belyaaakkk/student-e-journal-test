package com.belyak.test.domain.application.auth;

import com.belyak.test.domain.user.exception.EmailAlreadyExistsException;
import com.belyak.test.domain.common.InvalidTokenException;
import com.belyak.test.domain.user.exception.UsernameAlreadyExistsException;
import com.belyak.test.domain.user.model.User;
import com.belyak.test.domain.common.UserRepository;
import com.belyak.test.domain.user.value.Email;
import com.belyak.test.domain.user.value.Password;
import com.belyak.test.domain.user.value.Username;
import com.belyak.test.infrastructure.security.JwtService;
import com.belyak.test.infrastructure.security.UserPrincipal;
import com.belyak.test.presentation.dto.AuthenticationRequest;
import com.belyak.test.presentation.dto.AuthenticationResponse;
import com.belyak.test.presentation.dto.RegisterRequest;
import io.jsonwebtoken.JwtException;
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
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        User user = User.createNew(
                new Email(request.getEmail()),
                new Username(request.getUsername()),
                new Password(passwordEncoder.encode(request.getPassword()))
        );
        User saved = userRepository.save(user);

        UserPrincipal principal = UserPrincipal.fromDomain(saved);
        return buildAuthResponse(principal);
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
        return buildAuthResponse(principal);
    }

    public AuthenticationResponse refreshToken(String refreshToken) {
        try {
            final String subject = jwtService.extractUsername(refreshToken);

            if (subject == null) {
                throw new InvalidTokenException("Invalid refresh token: no subject found");
            }
            User user = userRepository.findByEmail(subject)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + subject));

            UserPrincipal userPrincipal = UserPrincipal.fromDomain(user);

            if (!jwtService.isRefreshTokenValid(refreshToken, userPrincipal)) {
                throw new InvalidTokenException("Invalid or expired refresh token");
            }

            return buildAuthResponse(userPrincipal);

        } catch (JwtException e) {
            throw new InvalidTokenException("Failed to refresh token: " + e.getMessage());
        }
    }

    private AuthenticationResponse buildAuthResponse(UserPrincipal principal) {
        String jwtToken = jwtService.generateToken(principal);
        String refreshToken = jwtService.generateRefreshToken(principal);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }
}