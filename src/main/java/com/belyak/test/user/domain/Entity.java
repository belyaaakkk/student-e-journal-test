package com.belyak.test.user.domain;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Abstract base class for domain entities with an ID and timestamps.
 */
@Getter
public abstract class Entity<ID> {
    protected final ID id;
    protected final LocalDateTime createdAt;
    protected final LocalDateTime updatedAt;

    /**
     * Creates an entity with the specified ID and current timestamps.
     *
     * @param id the entity ID
     */
    protected Entity(ID id) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Creates an entity for repository mapping with specified timestamps.
     *
     * @param id         the entity ID
     * @param createdAt  the creation timestamp
     * @param updatedAt  the last update timestamp
     */
    protected Entity(ID id, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "CreatedAt cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "UpdatedAt cannot be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}