package com.belyak.test.team.domain;

import lombok.Getter;

import java.util.Objects;

/**
 * Represents an access code for joining a team.
 */
@Getter
public final class AccessCode {
    private final String value;

    /**
     * Creates an access code with the specified value.
     *
     * @param value the access code (must be 8 uppercase alphanumeric characters)
     * @throws IllegalArgumentException if the value is invalid
     */
    public AccessCode(String value) {
        this.value = ValidationUtils.validateAccessCode(value);
    }

    /**
     * Checks if the provided code matches this access code.
     *
     * @param code the code to check
     * @return true if the codes match, false otherwise
     */
    public boolean matches(String code) {
        return Objects.equals(this.value, code != null ? code.toUpperCase() : null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccessCode)) return false;
        AccessCode that = (AccessCode) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}