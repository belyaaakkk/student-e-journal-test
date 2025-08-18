package com.belyak.test.team.domain;

import java.util.regex.Pattern;

/**
 * Utility class for shared validation logic across domain and entity layers.
 */
public final class ValidationUtils {
    private static final Pattern ACCESS_CODE_PATTERN = Pattern.compile("^[A-Z0-9]+$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    private ValidationUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Validates and normalizes an access code.
     *
     * @param code the access code to validate
     * @return the normalized (uppercase) access code
     * @throws IllegalArgumentException if the code is invalid
     */
    public static String validateAccessCode(String code) {
        if (code == null || code.length() != 8) {
            throw new IllegalArgumentException("Access code must be exactly 8 characters");
        }
        if (!ACCESS_CODE_PATTERN.matcher(code).matches()) {
            throw new IllegalArgumentException("Access code can only contain uppercase letters and numbers");
        }
        return code.toUpperCase();
    }

    /**
     * Validates an access password.
     *
     * @param password the password to validate
     * @return the password (or hashed password in a real implementation)
     * @throws IllegalArgumentException if the password is invalid
     */
    public static String validateAccessPassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Access password must be at least 6 characters");
        }
        return password; // In a real implementation, hash the password here
    }

    /**
     * Validates and normalizes a username.
     *
     * @param username the username to validate
     * @return the normalized (lowercase) username
     * @throws IllegalArgumentException if the username is invalid
     */
    public static String validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (username.length() < 3 || username.length() > 50) {
            throw new IllegalArgumentException("Username must be between 3 and 50 characters");
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException("Username can only contain letters, numbers, underscore, and hyphen");
        }
        return username.toLowerCase();
    }

    /**
     * Validates and normalizes an email.
     *
     * @param email the email to validate
     * @return the normalized (lowercase) email
     * @throws IllegalArgumentException if the email is invalid
     */
    public static String validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        return email.toLowerCase();
    }
}