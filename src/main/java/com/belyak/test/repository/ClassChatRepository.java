package com.belyak.test.repository;

import com.belyak.test.model.ClassChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassChatRepository extends JpaRepository<ClassChat, Long> {
    Optional<ClassChat> findBySchoolClass_Code(String code);
}
