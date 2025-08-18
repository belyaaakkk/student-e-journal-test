package com.belyak.test.user.entity;

import com.belyak.test.team.domain.ValidationUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * JPA embeddable representing a user's username.
 */
@Embeddable
public record Username(
        @NotNull
        @Size(min = 3, max = 50)
        @Column(name = "username", nullable = false, unique = true)
        String value
) {
    protected Username() {
        this(null);
    }

    public Username {
        value = ValidationUtils.validateUsername(value);
    }

    @Override
    public String toString() {
        return value;
    }
}