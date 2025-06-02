package com.belyak.test.model;

import jakarta.persistence.*;
import lombok.*;

// === FAQ ENTITY ===
@Entity
@Table(name = "faqs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Faq extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String questionKey; // e.g., faq.question.welcome

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answerKey; // e.g., faq.answer.welcome
}
