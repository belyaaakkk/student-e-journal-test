package com.belyak.test.model;

import com.belyak.test.model.enums.AnnouncementType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// === ANNOUNCEMENT ENTITY ===
@Entity
@Table(name = "announcements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Announcement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnnouncementType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author; // Typically an ADMIN or TEACHER

    @Column(nullable = false)
    private LocalDateTime publicationDate;

    @Column(nullable = true)
    private LocalDateTime expirationDate;
}
