package com.belyak.test.service;

import com.belyak.test.dto.EventReadDto;
import com.belyak.test.dto.HomeworkReadDto;
import com.belyak.test.mapper.EventMapper;
import com.belyak.test.model.*;
import com.belyak.test.model.enums.EventType;
import com.belyak.test.model.enums.HomeworkStatus;
import com.belyak.test.model.enums.Role;
import com.belyak.test.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final StudentRepository studentRepository;
    private final StudentService studentService;
    private final HomeworkService homeworkService;
    private final ScheduleRepository scheduleRepository;
    private final ParentRepository parentRepository;
    private final TeacherRepository teacherRepository;

    public List<EventReadDto> getEventsByMonth(int year, int month, User user) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = start.plusMonths(1).minusDays(1).atTime(23, 59, 59);

        List<EventReadDto> events = new ArrayList<>();

        if (user.getRole() == Role.STUDENT) {
            Student student = studentService.findByUsername(user.getUsername());
            SchoolClass schoolClass = student.getSchoolClass();

            getSchedules(startDateTime, endDateTime, events, schoolClass);
        } else if (user.getRole() == Role.PARENT) {
            Parent parent = parentRepository.findByUserUsername(user.getUsername()).get();

            SchoolClass schoolClass = parent.getChildren().get(0).getSchoolClass();

            getSchedules(startDateTime, endDateTime, events, schoolClass);
        } else if (user.getRole() == Role.TEACHER) {
            Teacher teacher = teacherRepository.findByUser_Username(user.getUsername()).get();
            List<Schedule> schedules = scheduleRepository.findByStartTimeBetweenAndTeacher(startDateTime, endDateTime, teacher);
            for (Schedule schedule : schedules) {
                EventReadDto dto = EventReadDto.builder()
                        .id(schedule.getId())
                        .title(schedule.getTitle())
                        .description(schedule.getDescription())
                        .startDate(schedule.getStartTime())
                        .endDate(schedule.getEndTime())
                        .teacherName(schedule.getTeacher().getUser().getLastname())
                        .subjectCode(schedule.getSubject().getMessageKey())
                        .eventType(schedule.getEventType())
                        .className(schedule.getSchoolClass().getMessageKey())
                        .room(schedule.getRoom())
                        .build();

                events.add(dto);
            }
        } else {
            List<Schedule> schedules = scheduleRepository.findByStartTimeBetween(startDateTime, endDateTime);
            for (Schedule schedule : schedules) {
                EventReadDto dto = EventReadDto.builder()
                        .id(schedule.getId())
                        .title(schedule.getTitle())
                        .description(schedule.getDescription())
                        .startDate(schedule.getStartTime())
                        .endDate(schedule.getEndTime())
                        .teacherName(schedule.getTeacher().getUser().getLastname())
                        .subjectCode(schedule.getSubject().getMessageKey())
                        .eventType(schedule.getEventType())
                        .className(schedule.getSchoolClass().getMessageKey())
                        .room(schedule.getRoom())
                        .build();

                events.add(dto);
            }
        }

        return events;
    }

    private void getSchedules(LocalDateTime startDateTime, LocalDateTime endDateTime, List<EventReadDto> events, SchoolClass schoolClass) {
        List<Schedule> schedules = scheduleRepository.findBySchoolClassIdAndStartTimeBetween(
                schoolClass.getId(), startDateTime, endDateTime
        );

        for (Schedule schedule : schedules) {
            EventReadDto dto = EventReadDto.builder()
                    .id(schedule.getId())
                    .title(schedule.getTitle())
                    .description(schedule.getDescription())
                    .startDate(schedule.getStartTime())
                    .endDate(schedule.getEndTime())
                    .teacherName(schedule.getTeacher().getUser().getLastname())
                    .subjectCode(schedule.getSubject().getMessageKey())
                    .eventType(schedule.getEventType())
                    .className(schedule.getSchoolClass().getMessageKey())
                    .room(schedule.getRoom())
                    .build();

            events.add(dto);
        }
    }

    public List<EventReadDto> getHomeworkEventsByMonth(int year, int month, User user) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<HomeworkReadDto> homeworkList;

        if (user.getRole() == Role.STUDENT) {
            homeworkList = homeworkService.getHomeworkForStudent(user.getUsername(), null);
        } else if (user.getRole() == Role.PARENT) {
            homeworkList = homeworkService.getHomeworkForParent(user.getUsername(), null);
        } else if (user.getRole() == Role.TEACHER) {
            homeworkList = homeworkService.getHomeworkForTeacher(user.getUsername(), null);
        } else {
            return List.of();
        }

        return homeworkList.stream()
                .filter(hw -> hw.getDueDate() != null &&
                              !hw.getDueDate().isBefore(startDate) &&
                              !hw.getDueDate().isAfter(endDate))
                .map(hw -> EventReadDto.builder()
                        .id(hw.getId())
                        .title(hw.getTitle())
                        .description(hw.getDescription())
                        .startDate(hw.getDueDate().atTime(9, 0)) // Условно 9:00 утра
                        .endDate(hw.getDueDate().atTime(10, 0)) // Условно 10:00
                        .eventType(EventType.HOMEWORK) // Используем строку "HOMEWORK"
                        .subjectCode(hw.getSubjectName())
                        .className(hw.getClassName())
                        .teacherName(hw.getTeacherFullName())
                        .homeworkStatus(hw.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

}