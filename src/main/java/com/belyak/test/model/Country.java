package com.belyak.test.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// === COUNTRY ENTITY ===
@Entity
@Getter
@Setter
@Table(name = "countries")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // e.g., UKRAINE, USA

    @Column(nullable = false, unique = true)
    private String messageKey; // e.g., label.country.ukraine
}
