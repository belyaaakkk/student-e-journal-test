package com.belyak.test.repository;

import com.belyak.test.model.Schedule;
import com.belyak.test.model.SchoolClass;
import com.belyak.test.model.Teacher;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @EntityGraph(attributePaths = {"subject", "teacher.user", "schoolClass"})
    List<Schedule> findBySchoolClassId(Long schoolClassId);

    @EntityGraph(attributePaths = {"subject", "teacher.user", "schoolClass"})
    List<Schedule> findBySchoolClassIdAndStartTimeBetween(Long schoolClassId, LocalDateTime start, LocalDateTime end);

    List<Schedule> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Schedule> findByStartTimeBetweenAndSchoolClass(LocalDateTime start, LocalDateTime end, SchoolClass schoolClass);

    List<Schedule> findByStartTimeBetweenAndSchoolClassIn(LocalDateTime start, LocalDateTime end, List<SchoolClass> schoolClasses);

    List<Schedule> findByStartTimeBetweenAndTeacher(LocalDateTime start, LocalDateTime end, Teacher teacher);

    List<Schedule> findByTeacherIdAndSubjectIdAndStartTimeBetween(Long teacherId, Long subjectId, LocalDateTime start, LocalDateTime end);

    List<Schedule> findAllByStartTimeBetween(LocalDateTime start, LocalDateTime end);

}