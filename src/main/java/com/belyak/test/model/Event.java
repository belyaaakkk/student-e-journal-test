package com.belyak.test.model;

import com.belyak.test.model.enums.EventType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// === EVENT ENTITY ===
@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // e.g., "Math Test: Algebra"

    @Column(columnDefinition = "TEXT")
    private String description; // Optional details

    @Column(nullable = false)
    private LocalDateTime startDate; // Event start (from Schedule or derived from Homework)

    @Column(nullable = false)
    private LocalDateTime endDate; // Event end (from Schedule or derived from Homework)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_class_id", nullable = false)
    private SchoolClass schoolClass;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType; // LESSON, TEST, MEETING, HOMEWORK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = true)
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "homework_id", nullable = true)
    private Homework homework;

    @Override
    protected void validateBeforePersist() {
        validateReferences();
    }

    @Override
    protected void validateBeforeUpdate() {
        validateReferences();
    }

    private void validateReferences() {
        if ((schedule != null && homework != null) || (schedule == null && homework == null)) {
            throw new IllegalStateException("Event must reference exactly one of Schedule or Homework");
        }
    }
}
