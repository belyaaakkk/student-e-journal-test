package com.belyak.test.repository;

import com.belyak.test.model.Parent;
import com.belyak.test.model.Student;
import com.belyak.test.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParentRepository extends JpaRepository<Parent, Long> {

    Optional<Parent> findByUserUsername(String username);

    Optional<Parent> findByUser_Username(String username);

}
