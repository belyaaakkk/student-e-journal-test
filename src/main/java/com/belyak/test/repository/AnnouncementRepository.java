package com.belyak.test.repository;

import com.belyak.test.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findAllByOrderByCreatedAtDesc();

    List<Announcement> findByPublicationDateBetween(LocalDateTime start, LocalDateTime end);
}
