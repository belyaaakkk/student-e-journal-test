package com.belyak.test.team.entity;

import com.belyak.test.team.domain.ValidationUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;

/**
 * JPA embeddable representing an access password for a team.
 */
@Getter
@Embeddable
public class AccessPasswordEntity {

    @Column(name = "access_password", nullable = false, unique = true)
    @NotNull
    @Size(min = 6)
    private String value;

    protected AccessPasswordEntity() {
    }

    /**
     * Creates an access password entity.
     *
     * @param password the password (must be at least 6 characters)
     * @throws IllegalArgumentException if the password is invalid
     */
    public AccessPasswordEntity(String password) {
        this.value = BCrypt.hashpw(
                ValidationUtils.validateAccessPassword(password),
                BCrypt.gensalt()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccessPasswordEntity)) return false;
        AccessPasswordEntity that = (AccessPasswordEntity) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}