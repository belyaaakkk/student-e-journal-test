package com.belyak.test.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// === SCHOOL CLASS ENTITY ===
@Entity
@Getter
@Setter
@Table(name = "school_classes")
public class SchoolClass extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // e.g., TEN_A, ELEVEN_B

    @Column(nullable = false, unique = true)
    private String messageKey; // e.g., label.class.ten_a
}
