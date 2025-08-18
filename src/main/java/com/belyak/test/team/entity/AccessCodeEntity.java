package com.belyak.test.team.entity;

import com.belyak.test.team.domain.ValidationUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.Objects;

/**
 * JPA embeddable representing an access code for a team.
 */
@Getter
@Embeddable
public class AccessCodeEntity {

    @Column(name = "access_code", nullable = false, unique = true, length = 8)
    @NotNull
    @Size(min = 8, max = 8)
    private String value;

    protected AccessCodeEntity() {
    }

    /**
     * Creates an access code entity.
     *
     * @param code the access code (must be 8 uppercase alphanumeric characters)
     * @throws IllegalArgumentException if the code is invalid
     */
    public AccessCodeEntity(String code) {
        this.value = ValidationUtils.validateAccessCode(code);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccessCodeEntity)) return false;
        AccessCodeEntity that = (AccessCodeEntity) o;
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