package com.belyak.test.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

// === CLASS CHAT ENTITY ===
@Entity
@Table(name = "class_chats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassChat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_class_id", nullable = false, unique = true)
    private SchoolClass schoolClass;

    @OneToMany(mappedBy = "classChat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();
}
