package com.belyak.test.service;

import com.belyak.test.dto.AnnouncementCreateDto;
import com.belyak.test.exception.EntityNotFoundException;
import com.belyak.test.model.Announcement;
import com.belyak.test.model.User;
import com.belyak.test.model.enums.AnnouncementType;
import com.belyak.test.repository.AnnouncementRepository;
import com.belyak.test.repository.TeacherRepository;
import com.belyak.test.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.belyak.test.model.enums.StatusCodes.ENTITY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;

    public List<Announcement> getAllAnnouncements(AnnouncementType type) {
        return announcementRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(announcement -> type == null || type.equals(announcement.getType()))
                .toList();
    }

    @Transactional
    public void createAnnouncement(AnnouncementCreateDto dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        ENTITY_NOT_FOUND.name(),
                        "User with username %s not found".formatted(username)));

        Announcement announcement = Announcement.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .type(dto.getType())
                .author(user)
                .publicationDate(LocalDateTime.now())
                .expirationDate(LocalDateTime.now().plusMonths(1))
                .build();

        announcementRepository.save(announcement);
    }

    @Transactional
    public void deleteAnnouncement(Long id, String username) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        ENTITY_NOT_FOUND.name(),
                        "Announcement with id %d not found".formatted(id)));

        if (!announcement.getAuthor().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not allowed to delete this announcement");
        }

        announcementRepository.delete(announcement);
    }
}
