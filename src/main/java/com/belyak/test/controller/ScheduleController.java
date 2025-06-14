package com.belyak.test.controller;

import com.belyak.test.exception.EntityNotFoundException;
import com.belyak.test.mapper.ParentMapper;
import com.belyak.test.mapper.StudentMapper;
import com.belyak.test.mapper.TeacherMapper;
import com.belyak.test.model.*;
import com.belyak.test.model.enums.Role;
import com.belyak.test.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

import static com.belyak.test.model.enums.Role.*;
import static com.belyak.test.model.enums.StatusCodes.ENTITY_NOT_FOUND;

@Controller
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleRepository scheduleRepository;
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final ParentMapper parentMapper;
    private final TeacherMapper teacherMapper;
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;

    @GetMapping("/schedule")
    public String getSchedule(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestParam(value = "weekOffset", defaultValue = "0") int weekOffset,
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "8") int size,
                              Model model) {
        // поиск пользователя
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND.name(), "User not found"));

        Role role = user.getRole();
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1).plusWeeks(weekOffset);
        LocalDate sunday = monday.plusDays(6);

        List<Schedule> allSchedules = new ArrayList<>();

        switch (role) {
            case STUDENT -> {
                Student student = studentRepository.findByUserUsername(user.getUsername())
                        .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND.name(), "Student not found"));
                Long classId = student.getSchoolClass().getId();
                allSchedules = scheduleRepository.findBySchoolClassIdAndStartTimeBetween(
                        classId, monday.atStartOfDay(), sunday.atTime(23, 59, 59));
                model.addAttribute("student", studentMapper.toReadStudentDto(student));
            }
            case TEACHER -> {
                Teacher teacher = teacherRepository.findByUser_Username(user.getUsername())
                        .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND.name(), "Teacher not found"));
                Subject subject = teacher.getSubject();
                if (subject != null) {
                    allSchedules = scheduleRepository.findByTeacherIdAndSubjectIdAndStartTimeBetween(
                            teacher.getId(), subject.getId(), monday.atStartOfDay(), sunday.atTime(23, 59, 59));
                }
                model.addAttribute("teacher", teacherMapper.toReadTeacherDto(teacher));
            }
            case PARENT -> {
                Parent parent = parentRepository.findByUserUsername(user.getUsername())
                        .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND.name(), "Parent not found"));
                Set<Long> classIds = parent.getChildren().stream()
                        .map(child -> child.getSchoolClass().getId())
                        .collect(Collectors.toSet());
                for (Long classId : classIds) {
                    allSchedules.addAll(scheduleRepository.findBySchoolClassIdAndStartTimeBetween(
                            classId, monday.atStartOfDay(), sunday.atTime(23, 59, 59)));
                }
                model.addAttribute("parent", parentMapper.toReadParentDto(parent));
            }
            case ADMIN -> {
                allSchedules = scheduleRepository.findAllByStartTimeBetween(
                        monday.atStartOfDay(), sunday.atTime(23, 59, 59));
            }
            default -> throw new AccessDeniedException("Unsupported role: " + role);
        }

        allSchedules.sort(Comparator.comparing(Schedule::getStartTime));

        // Пагинация вручную
        int start = Math.min(page * size, allSchedules.size());
        int end = Math.min(start + size, allSchedules.size());
        List<Schedule> pageContent = allSchedules.subList(start, end);

        Page<Schedule> schedulesPage = new PageImpl<>(pageContent, PageRequest.of(page, size), allSchedules.size());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.forLanguageTag("uk"));
        String weekRange = monday.format(formatter) + " – " + sunday.format(formatter);

        model.addAttribute("weekRange", weekRange);
        model.addAttribute("schedulePage", schedulesPage);
        model.addAttribute("schedules", schedulesPage.getContent());
        model.addAttribute("weekOffset", weekOffset);
        model.addAttribute("role", role.name());

        return "schedule";
    }


}
