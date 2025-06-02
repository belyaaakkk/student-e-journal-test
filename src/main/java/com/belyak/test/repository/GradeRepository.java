package com.belyak.test.repository;


import com.belyak.test.dto.ReadGradeDto;
import com.belyak.test.model.Grade;
import com.belyak.test.model.Student;
import com.belyak.test.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {

    List<Grade> findAllByStudent(Student student);

    List<Grade> findAllByTeacher(Teacher teacher);
}
