package com.belyak.test.repository;

import com.belyak.test.model.Homework;
import com.belyak.test.model.SchoolClass;
import com.belyak.test.model.Teacher;
import com.belyak.test.model.enums.HomeworkStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HomeworkRepository extends JpaRepository<Homework, Long> {

    List<Homework> findBySchoolClass(SchoolClass schoolClass);

    List<Homework> findBySchoolClassAndStatus(SchoolClass schoolClass, HomeworkStatus status);

    List<Homework> findByTeacher(Teacher teacher);

    List<Homework> findByTeacherAndStatus(Teacher teacher, HomeworkStatus status);

    List<Homework> findByDueDateBetween(LocalDate start, LocalDate end);

    List<Homework> findByDueDateBetweenAndSchoolClass(LocalDate start, LocalDate end, SchoolClass schoolClass);

    List<Homework> findByDueDateBetweenAndSchoolClassIn(LocalDate start, LocalDate end, List<SchoolClass> schoolClasses);

    List<Homework> findByDueDateBetweenAndTeacher(LocalDate start, LocalDate end, Teacher teacher);
}
