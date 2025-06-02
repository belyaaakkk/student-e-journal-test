package com.belyak.test.service;

import com.belyak.test.dto.CreateGradeDto;
import com.belyak.test.dto.GradeForm;
import com.belyak.test.dto.ReadGradeDto;
import com.belyak.test.exception.EntityNotFoundException;
import com.belyak.test.mapper.GradeMapper;
import com.belyak.test.model.*;
import com.belyak.test.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.belyak.test.model.enums.StatusCodes.ENTITY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final GradeMapper gradeMapper;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final ParentRepository parentRepository;


    public List<ReadGradeDto> getGradesForStudent(String username) {
        Student student = studentRepository.findByUserUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        ENTITY_NOT_FOUND.name(),
                        "Student with username %s not found".formatted(username)));
        return gradeRepository.findAllByStudent(student).stream()
                .map(gradeMapper::toReadGradeDto)
                .collect(Collectors.toList());
    }

    public List<ReadGradeDto> getGradesForParent(String username) {
        Parent parent = parentRepository.findByUser_Username(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        ENTITY_NOT_FOUND.name(),
                        "Parent with username %s not found".formatted(username)
                ));

        Student student = parent.getChildren().get(0);

        return gradeRepository.findAllByStudent(student).stream()
                .map(gradeMapper::toReadGradeDto)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        gradeRepository.deleteById(id);
    }


    public List<ReadGradeDto> getGradesForTeacher(String username) {
        Teacher teacher = teacherRepository.findByUser_Username(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        ENTITY_NOT_FOUND.name(),
                        "Teacher with username %s not found".formatted(username)));

        return gradeRepository.findAllByTeacher(teacher).stream()
                .map(gradeMapper::toReadGradeDto)
                .collect(Collectors.toList());
    }


    public List<ReadGradeDto> getAllGraders() {
        return gradeRepository.findAll().stream()
                .map(gradeMapper::toReadGradeDto)
                .collect(Collectors.toList());
    }

    public void save(Grade grade) {
        gradeRepository.save(grade);
    }

    public Optional<Grade> findById(Long id) {
        return gradeRepository.findById(id);
    }

    public void addGrade(GradeForm form) {
        Student student = studentRepository.findById(form.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND.name(), "Student not found"));
        Teacher teacher = teacherRepository.findById(form.getTeacherId())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND.name(), "Teacher not found"));
        Subject subject = subjectRepository.findById(form.getSubjectId())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND.name(), "Subject not found"));

        Grade grade = Grade.builder()
                .student(student)
                .teacher(teacher)
                .subject(subject)
                .score(form.getScore())
                .date(form.getDate())
                .comment(form.getComment())
                .build();

        gradeRepository.save(grade);
    }

}
