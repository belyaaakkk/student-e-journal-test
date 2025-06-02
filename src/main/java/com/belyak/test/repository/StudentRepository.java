package com.belyak.test.repository;

import com.belyak.test.model.Student;
import com.belyak.test.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUserUsername(String username);

    Optional<Student> findByUser(User user);

    Page<Student> findBySchoolClass_Code(String code, Pageable pageable);
}
