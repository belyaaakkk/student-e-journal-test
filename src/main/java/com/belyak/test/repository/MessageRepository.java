package com.belyak.test.repository;

import com.belyak.test.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByClassChat_SchoolClass_CodeOrderByTimestampAsc(String code);
}