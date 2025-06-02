package com.belyak.test.repository;

import com.belyak.test.model.Event;
import com.belyak.test.model.SchoolClass;
import com.belyak.test.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByStartDateBetween(LocalDateTime start, LocalDateTime end);

    List<Event> findByStartDateBetweenAndSchoolClass(LocalDateTime start, LocalDateTime end, SchoolClass schoolClass);

    List<Event> findByStartDateBetweenAndSchoolClassIn(LocalDateTime start, LocalDateTime end, List<SchoolClass> schoolClasses);

    @Query("SELECT e FROM Event e WHERE e.startDate BETWEEN :start AND :end AND " +
           "(e.schedule.teacher = :teacher OR e.homework.teacher = :teacher)")
    List<Event> findByStartDateBetweenAndTeacher(LocalDateTime start, LocalDateTime end, Teacher teacher);
}