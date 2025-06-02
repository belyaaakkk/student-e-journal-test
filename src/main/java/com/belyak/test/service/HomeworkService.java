package com.belyak.test.service;

import com.belyak.test.dto.HomeworkCreateDto;
import com.belyak.test.dto.HomeworkReadDto;
import com.belyak.test.exception.EntityNotFoundException;
import com.belyak.test.mapper.HomeworkMapper;
import com.belyak.test.model.*;
import com.belyak.test.model.enums.HomeworkStatus;
import com.belyak.test.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.belyak.test.model.enums.StatusCodes.ENTITY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class HomeworkService {

    private final HomeworkRepository homeworkRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final HomeworkMapper homeworkMapper;
    private final SchoolClassRepository schoolClassRepository;
    private final ParentRepository parentRepository;

    public List<HomeworkReadDto> getHomeworkForStudent(String username, HomeworkStatus status) {
        Student student = studentRepository.findByUserUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        ENTITY_NOT_FOUND.name(),
                        "Student with username %s not found".formatted(username)));

        List<Homework> homeworkList = (status != null)
                ? homeworkRepository.findBySchoolClassAndStatus(student.getSchoolClass(), status)
                : homeworkRepository.findBySchoolClass(student.getSchoolClass());

        return homeworkMapper.toHomeworkReadDtoList(homeworkList);
    }

    public List<HomeworkReadDto> getHomeworkForParent(String username, HomeworkStatus status) {
        Parent parent = parentRepository.findByUserUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        ENTITY_NOT_FOUND.name(),
                        "Parent with username %s not found".formatted(username)));

        List<Student> children = parent.getChildren();
        if (children.isEmpty()) {
            return List.of();
        }

        // пока показываем домашки только по первому ребенку
        Student firstChild = children.get(0);

        List<Homework> homeworkList = (status != null)
                ? homeworkRepository.findBySchoolClassAndStatus(firstChild.getSchoolClass(), status)
                : homeworkRepository.findBySchoolClass(firstChild.getSchoolClass());

        return homeworkMapper.toHomeworkReadDtoList(homeworkList);
    }



    public List<HomeworkReadDto> getHomeworkForTeacher(String username, HomeworkStatus status) {
        Teacher teacher = teacherRepository.findByUser_Username(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        ENTITY_NOT_FOUND.name(),
                        "Teacher with username %s not found".formatted(username)));

        List<Homework> homeworkList = (status != null)
                ? homeworkRepository.findByTeacherAndStatus(teacher, status)
                : homeworkRepository.findByTeacher(teacher);

        return homeworkMapper.toHomeworkReadDtoList(homeworkList);
    }

    @Transactional
    public void createHomework(HomeworkCreateDto dto, String username) {
        Teacher teacher = teacherRepository.findByUser_Username(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        ENTITY_NOT_FOUND.name(),
                        "Teacher with username %s not found".formatted(username)));

        Subject subject = subjectRepository.findSubjectByCode(dto.getSubjectCode())
                .orElseThrow(() -> new EntityNotFoundException(
                        ENTITY_NOT_FOUND.name(),
                        "Subject with code %s not found".formatted(dto.getSubjectCode())));

        SchoolClass schoolClass = schoolClassRepository.findByCode(dto.getClassCode())
                .orElseThrow(() -> new EntityNotFoundException(
                        ENTITY_NOT_FOUND.name(),
                        "SchoolClass with code %s not found".formatted(dto.getClassCode())));

        HomeworkStatus status = HomeworkStatus.ASSIGNED;
        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            status = HomeworkStatus.valueOf(dto.getStatus());
        }

        Homework homework = Homework.builder()
                .subject(subject)
                .teacher(teacher)
                .schoolClass(schoolClass)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .dueDate(dto.getDueDate().toLocalDate())
                .status(status)
                .build();

        homeworkRepository.save(homework);
    }

    @Transactional
    public void deleteHomework(Long homeworkId, String username) {
        Teacher teacher = teacherRepository.findByUser_Username(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        ENTITY_NOT_FOUND.name(),
                        "Teacher with username %s not found".formatted(username)));

        Homework homework = homeworkRepository.getReferenceById(homeworkId);
        if (!homework.getTeacher().equals(teacher)) {
            throw new SecurityException("Access denied to delete this homework.");
        }
        homeworkRepository.delete(homework);
    }
}
