package com.belyak.test.service;

import com.belyak.test.dto.CreateTeacherDto;
import com.belyak.test.dto.ReadTeacherDto;
import com.belyak.test.dto.UpdateTeacherDto;
import com.belyak.test.exception.EntityNotFoundException;
import com.belyak.test.mapper.TeacherMapper;
import com.belyak.test.model.Country;
import com.belyak.test.model.Subject;
import com.belyak.test.model.Teacher;
import com.belyak.test.model.User;
import com.belyak.test.model.enums.Role;
import com.belyak.test.repository.CountryRepository;
import com.belyak.test.repository.SubjectRepository;
import com.belyak.test.repository.TeacherRepository;
import com.belyak.test.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.belyak.test.model.enums.StatusCodes.ENTITY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final CountryRepository countryRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherMapper teacherMapper;
    private final UserRepository userRepository;

    public List<ReadTeacherDto> getAllTeachers() {
        return teacherRepository.findAll().stream()
                .map(teacherMapper::toReadTeacherDto)
                .collect(Collectors.toList());
    }

    public ReadTeacherDto getTeacherById(Long id) {
        return this.teacherRepository.findById(id)
                .map(teacherMapper::toReadTeacherDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        ENTITY_NOT_FOUND.name(),
                        "Teacher with id %d not found".formatted(id)));
    }

    @Transactional
    public void updateTeacher(Long id, UpdateTeacherDto teacherDto) {
        Teacher teacher = this.teacherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        ENTITY_NOT_FOUND.name(),
                        "Teacher with id %d not found".formatted(id)));

        User user = teacher.getUser();
        user.setFirstname(teacherDto.getFirstname());
        user.setLastname(teacherDto.getLastname());
        user.setEmail(teacherDto.getEmail());
        user.setPhoneNumber(teacherDto.getPhoneNumber());
        user.setBio(teacherDto.getBio());

        if (teacherDto.getSubjectCode() != null) {
            Subject subject = subjectRepository.findSubjectByCode(teacherDto.getSubjectCode())
                    .orElseThrow(() -> new EntityNotFoundException(
                            ENTITY_NOT_FOUND.name(),
                            "Subject with code %s not found".formatted(teacherDto.getSubjectCode())));
            teacher.setSubject(subject);
        } else {
            teacher.setSubject(null);
        }
    }

    @Transactional
    public void createTeacher(CreateTeacherDto dto) {
        Country country = countryRepository.findByCode(dto.getCountryCode())
                .orElseThrow(() -> new EntityNotFoundException(
                        ENTITY_NOT_FOUND.name(),
                        "Country with code %s not found".formatted(dto.getCountryCode())));

        Subject subject = subjectRepository.findSubjectByCode(dto.getSubjectCode())
                .orElseThrow(() -> new EntityNotFoundException(
                        ENTITY_NOT_FOUND.name(),
                        "Subject with code %s not found".formatted(dto.getSubjectCode())));

        User user = User.builder()
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .email(dto.getEmail())
                .username(dto.getUsername())
                .phoneNumber(dto.getPhoneNumber())
                .dateOfBirth(dto.getDateOfBirth())
                .bio(dto.getBio())
                .country(country)
                .role(Role.TEACHER)
                .accountStatus(true)
                .password("teacher")
                .build();

        userRepository.save(user);

        Teacher teacher = Teacher.builder()
                .user(user)
                .subject(subject)
                .build();

        teacherRepository.save(teacher);
    }

    public void deleteTeacherById(Long teacherId) {
        this.teacherRepository.deleteById(teacherId);
    }
}
