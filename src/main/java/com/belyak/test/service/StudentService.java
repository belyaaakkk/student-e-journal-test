package com.belyak.test.service;

import com.belyak.test.dto.CreateStudentDto;
import com.belyak.test.dto.EditStudentDto;
import com.belyak.test.dto.ReadStudentDto;
import com.belyak.test.exception.EntityNotFoundException;
import com.belyak.test.mapper.StudentMapper;
import com.belyak.test.model.Country;
import com.belyak.test.model.SchoolClass;
import com.belyak.test.model.Student;
import com.belyak.test.model.User;
import com.belyak.test.model.enums.Role;
import com.belyak.test.repository.CountryRepository;
import com.belyak.test.repository.SchoolClassRepository;
import com.belyak.test.repository.StudentRepository;
import com.belyak.test.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.belyak.test.model.enums.StatusCodes.ENTITY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final CountryRepository countryRepository;
    private final StudentMapper studentMapper;
    private final UserRepository userRepository;

    public Page<ReadStudentDto> getAllStudentsFiltered(String classFilter, Pageable pageable) {
        Page<Student> page = (classFilter == null || classFilter.isBlank())
                ? studentRepository.findAll(pageable)
                : studentRepository.findBySchoolClass_Code(classFilter, pageable);

        return page.map(studentMapper::toReadStudentDto);
    }


    public ReadStudentDto getStudentById(Long id) {
        return this.studentRepository.findById(id)
                .map(studentMapper::toReadStudentDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        ENTITY_NOT_FOUND.name(),
                        "Student with id %d not found".formatted(id)));
    }

    public List<ReadStudentDto> getAllStudents() {
        return this.studentRepository.findAll()
                .stream().map(studentMapper::toReadStudentDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateStudent(Long id, EditStudentDto studentDto) {
        Student student = this.studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        ENTITY_NOT_FOUND.name(),
                        "Student with id %d not found".formatted(id)));
        User user = student.getUser();

        SchoolClass schoolClass = schoolClassRepository.findByCode(studentDto.getClassCode())
                .orElseThrow(() -> new EntityNotFoundException(
                        ENTITY_NOT_FOUND.name(),
                        "SchoolClass with code %s not found".formatted(student.getSchoolClass())));

        student.setSchoolClass(schoolClass);
        user.setFirstname(studentDto.getFirstName());
        user.setLastname(studentDto.getLastName());
        user.setEmail(studentDto.getEmail());
        user.setPhoneNumber(studentDto.getPhoneNumber());
        user.setDateOfBirth(studentDto.getDateOfBirth());
        student.setEnrollmentDate(studentDto.getEnrollmentDate());
        user.setBio(studentDto.getBio());
    }

    public Student findByUsername(String username) {
        return studentRepository.findByUserUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        ENTITY_NOT_FOUND.name(),
                        "User with id %s not found".formatted(username)));
    }

    @Transactional
    public void createStudent(CreateStudentDto dto) {
        Country country = countryRepository.findByCode(dto.getCountryCode())
                .orElseThrow(() -> new EntityNotFoundException(
                        ENTITY_NOT_FOUND.name(),
                        "Country with code %s not found".formatted(dto.getCountryCode())));

        SchoolClass schoolClass = schoolClassRepository.findByCode(dto.getClassCode())
                .orElseThrow(() -> new EntityNotFoundException(
                        ENTITY_NOT_FOUND.name(),
                        "SchoolClass with code %s not found".formatted(dto.getClassCode())));

        User user = User.builder()
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .dateOfBirth(dto.getDateOfBirth())
                .bio(dto.getBio())
                .country(country)
                .role(Role.STUDENT)
                .accountStatus(true)
                .password("student")
                .build();

        userRepository.save(user);

        Student student = Student.builder()
                .user(user)
                .schoolClass(schoolClass)
                .enrollmentDate(dto.getEnrollmentDate())
                .build();

        studentRepository.save(student);
    }

    public void deleteStudentById(Long id) {
        if (!studentRepository.existsById(id)) {
            System.out.println("Student with id " + id + " not found");
        }
        studentRepository.deleteById(id);
    }
}
