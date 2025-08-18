package com.belyak.test.user.domain;

import com.belyak.test.team.domain.ValidationUtils;
import lombok.Getter;

import java.util.Objects;

/**
 * Value object representing a user's username.
 */
@Getter
public final class Username {

    private final String value;

    /**
     * Creates a username with the specified value.
     *
     * @param value the username (must be 3-50 characters, letters, numbers, underscore, or hyphen)
     * @throws IllegalArgumentException if the value is invalid
     */
    public Username(String value) {
        this.value = ValidationUtils.validateUsername(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Username)) return false;
        Username username = (Username) o;
        return Objects.equals(value, username.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}