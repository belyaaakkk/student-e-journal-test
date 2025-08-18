package com.belyak.test.team.domain;

import lombok.Getter;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;

/**
 * Represents an access password for joining a team.
 */
@Getter
public final class AccessPassword {
    private final String value;

    /**
     * Creates an access password with the specified value.
     *
     * @param value the password (must be at least 6 characters)
     * @throws IllegalArgumentException if the value is invalid
     */
    public AccessPassword(String value) {
        this.value = BCrypt.hashpw(
                ValidationUtils.validateAccessPassword(value),
                BCrypt.gensalt()
        );
    }

    /**
     * Checks if the provided password matches this access password.
     *
     * @param password the password to check
     * @return true if the passwords match, false otherwise
     */
    public boolean matches(String password) {
        return password != null && BCrypt.checkpw(password, this.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccessPassword)) return false;
        AccessPassword that = (AccessPassword) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}