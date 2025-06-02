package com.belyak.test.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// === SUBJECT ENTITY ===
@Entity
@Getter
@Setter
@Table(name = "subjects")
public class Subject extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // e.g., MATH, PHYSICS

    @Column(nullable = false, unique = true)
    private String messageKey; // e.g., label.subject.math

    @Column(nullable = false)
    private boolean visible = true;
}
