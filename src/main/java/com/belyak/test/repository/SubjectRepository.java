package com.belyak.test.repository;

import com.belyak.test.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Optional<Subject> findSubjectByCode(String subjectCode);

    Optional<Subject> findSubjectByMessageKey(String messageKey);
}
