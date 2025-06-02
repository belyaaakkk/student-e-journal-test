package com.belyak.test.model;

import com.belyak.test.model.enums.HomeworkStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// === HOMEWORK ENTITY ===
@Entity
@Table(name = "homeworks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Homework extends BaseEntity {

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
    private String title; // e.g., "Physics Homework"

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HomeworkStatus status = HomeworkStatus.ASSIGNED;

    @OneToMany(mappedBy = "homework", fetch = FetchType.LAZY)
    private List<Event> events = new ArrayList<>(); // Added for Event relationship
}
