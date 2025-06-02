package com.belyak.test.repository;

import com.belyak.test.model.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {

    @Query("SELECT sc.code FROM SchoolClass sc ORDER BY sc.code")
    List<String> findAllClassCodes();

    Optional<SchoolClass> findByCode(String code);

}
