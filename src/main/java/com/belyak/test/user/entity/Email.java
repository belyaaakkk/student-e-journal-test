package com.belyak.test.user.entity;

import com.belyak.test.team.domain.ValidationUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * JPA embeddable representing a user's email.
 */
@Embeddable
public record Email(
        @NotNull
        @Size(max = 255)
        @Column(name = "email", nullable = false, unique = true)
        String value
) {
    protected Email() {
        this(null);
    }

    public Email {
        value = ValidationUtils.validateEmail(value);
    }
}