package com.belyak.test.user.domain;

import com.belyak.test.team.domain.ValidationUtils;
import lombok.Getter;

import java.util.Objects;

/**
 * Value object representing a user's email.
 */
@Getter
public final class Email {

    private final String value;

    /**
     * Creates an email with the specified value.
     *
     * @param value the email (must match standard email format)
     * @throws IllegalArgumentException if the value is invalid
     */
    public Email(String value) {
        this.value = ValidationUtils.validateEmail(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email)) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}