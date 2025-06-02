package com.belyak.test.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// === BASE ENTITY FOR COMMON FIELDS ===
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    @Column(name = "created_at", nullable = true, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        validateBeforePersist();
        this.createdAt = this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        validateBeforeUpdate();
        this.updatedAt = LocalDateTime.now();
    }

    protected void validateBeforePersist() {
        // Default empty implementation
    }

    protected void validateBeforeUpdate() {
        // Default empty implementation
    }
}
