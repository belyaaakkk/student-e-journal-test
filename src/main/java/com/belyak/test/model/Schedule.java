package com.belyak.test.model;

import com.belyak.test.model.enums.EventType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// === SCHEDULE ENTITY ===
@Entity
@Table(name = "schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_class_id", nullable = false)
    private SchoolClass schoolClass;

    @Column(nullable = false)
    private String title; // e.g., "Math Lesson: Algebra"

    @Column(columnDefinition = "TEXT")
    private String description; // Optional details

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private String room; // e.g., Room 101

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType; // LESSON, TEST, MEETING

    @OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY)
    private List<Event> events = new ArrayList<>(); // Added for Event relationship
}
