package com.belyak.test.domain.application.auth;

import com.belyak.test.domain.user.exception.InvalidEmailException;
import com.belyak.test.domain.user.exception.InvalidPasswordException;
import com.belyak.test.domain.user.exception.InvalidUsernameException;

import java.util.regex.Pattern;

public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private static final Pattern USERNAME_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_-]{3,50}$"
    );

    public static String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidEmailException("Email cannot be null or empty");
        }

        String trimmedEmail = email.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            throw new InvalidEmailException("Invalid email format");
        }

        return trimmedEmail;
    }

    public static String validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidUsernameException("Username cannot be null or empty");
        }

        String trimmedUsername = username.trim();
        if (!USERNAME_PATTERN.matcher(trimmedUsername).matches()) {
            throw new InvalidUsernameException("Username must be 3-50 characters long and contain only letters, numbers, underscore, or hyphen");
        }

        return trimmedUsername;
    }

    public static String validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new InvalidPasswordException("Password cannot be null or empty");
        }
        return password;
    }
}
