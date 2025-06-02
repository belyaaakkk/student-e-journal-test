package com.belyak.test.service;

import com.belyak.test.dto.EventReadDto;
import com.belyak.test.mapper.EventMapper;
import com.belyak.test.model.*;
import com.belyak.test.model.enums.EventType;
import com.belyak.test.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ScheduleRepository scheduleRepository;
    private final HomeworkRepository homeworkRepository;
    private final AnnouncementRepository announcementRepository;
    private final EventMapper eventMapper;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;
    private final SchoolClassRepository schoolClassRepository;

    public List<EventReadDto> getEventsByMonth(int year, int month, UserDetails userDetails) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = start.plusMonths(1).minusDays(1).atTime(23, 59, 59);
        LocalDate end = endDateTime.toLocalDate(); // Define 'end' by converting endDateTime to LocalDate

        User user = (User) userDetails;
        List<EventReadDto> events = new ArrayList<>();

        switch (user.getRole()) {
            case STUDENT:
                Student student = studentRepository.findById(user.getId())
                        .orElseThrow(() -> new UsernameNotFoundException("Student not found for username: " + user.getUsername()));
                SchoolClass schoolClass = student.getSchoolClass();
                events.addAll(mapSchedulesToEvents(scheduleRepository.findByStartTimeBetweenAndSchoolClass(startDateTime, endDateTime, schoolClass)));
                events.addAll(mapHomeworksToEvents(homeworkRepository.findByDueDateBetweenAndSchoolClass(start, end, schoolClass)));
                events.addAll(mapAnnouncementsToEvents(announcementRepository.findByPublicationDateBetween(startDateTime, endDateTime)));
                break;

            case TEACHER:
                Teacher teacher = teacherRepository.findById(user.getId())
                        .orElseThrow(() -> new UsernameNotFoundException("Teacher not found for username: " + user.getUsername()));
                events.addAll(mapSchedulesToEvents(scheduleRepository.findByStartTimeBetweenAndTeacher(startDateTime, endDateTime, teacher)));
                events.addAll(mapHomeworksToEvents(homeworkRepository.findByDueDateBetweenAndTeacher(start, end, teacher)));
                events.addAll(mapAnnouncementsToEvents(announcementRepository.findByPublicationDateBetween(startDateTime, endDateTime)));
                break;

            case PARENT:
                Parent parent = parentRepository.findById(user.getId())
                        .orElseThrow(() -> new UsernameNotFoundException("Parent not found for username: " + user.getUsername()));
                List<SchoolClass> childClasses = parent.getChildren().stream()
                        .map(Student::getSchoolClass)
                        .distinct()
                        .toList();
                events.addAll(mapSchedulesToEvents(scheduleRepository.findByStartTimeBetweenAndSchoolClassIn(startDateTime, endDateTime, childClasses)));
                events.addAll(mapHomeworksToEvents(homeworkRepository.findByDueDateBetweenAndSchoolClassIn(start, end, childClasses)));
                events.addAll(mapAnnouncementsToEvents(announcementRepository.findByPublicationDateBetween(startDateTime, endDateTime)));
                break;

            case ADMIN:
                events.addAll(mapSchedulesToEvents(scheduleRepository.findByStartTimeBetween(startDateTime, endDateTime)));
                events.addAll(mapHomeworksToEvents(homeworkRepository.findByDueDateBetween(start, end)));
                events.addAll(mapAnnouncementsToEvents(announcementRepository.findByPublicationDateBetween(startDateTime, endDateTime)));
                break;

            default:
                throw new IllegalStateException("Unsupported role: " + user.getRole());
        }

        System.out.println("Total events mapped: " + events.size());
        events.forEach(e -> System.out.println("Event: " + e.getTitle() + ", Start: " + e.getStartDate()));
        return events;
    }

    private List<EventReadDto> mapSchedulesToEvents(List<Schedule> schedules) {
        System.out.println("Mapping " + schedules.size() + " schedules");
        return schedules.stream()
                .map(schedule -> EventReadDto.builder()
                        .id(schedule.getId())
                        .title(schedule.getTitle())
                        .description(schedule.getDescription())
                        .startDate(schedule.getStartTime())
                        .endDate(schedule.getEndTime())
                        .className(schedule.getSchoolClass().getCode())
                        .eventType(schedule.getEventType())
                        .subjectCode(schedule.getSubject().getCode())
                        .teacherName(schedule.getTeacher().getUser().getFirstname() + " " + schedule.getTeacher().getUser().getLastname())
                        .room(schedule.getRoom())
                        .build())
                .collect(Collectors.toList());
    }

    private List<EventReadDto> mapHomeworksToEvents(List<Homework> homeworks) {
        System.out.println("Mapping " + homeworks.size() + " homeworks");
        return homeworks.stream()
                .map(homework -> EventReadDto.builder()
                        .id(homework.getId())
                        .title(homework.getTitle())
                        .description(homework.getDescription())
                        .startDate(homework.getDueDate().atStartOfDay())
                        .endDate(homework.getDueDate().atTime(23, 59, 59))
                        .className(homework.getSchoolClass().getCode())
                        .eventType(EventType.HOMEWORK)
                        .subjectCode(homework.getSubject().getCode())
                        .teacherName(homework.getTeacher().getUser().getFirstname() + " " + homework.getTeacher().getUser().getLastname())
                        .homeworkId(homework.getId())
                        .homeworkStatus(homework.getStatus().toString())
                        .build())
                .collect(Collectors.toList());
    }

    private List<EventReadDto> mapAnnouncementsToEvents(List<Announcement> announcements) {
        System.out.println("Mapping " + announcements.size() + " announcements");
        return announcements.stream()
                .filter(a -> a.getExpirationDate() == null || a.getExpirationDate().isAfter(LocalDateTime.now()))
                .map(announcement -> EventReadDto.builder()
                        .id(announcement.getId())
                        .title(announcement.getTitle())
                        .description(announcement.getContent())
                        .startDate(announcement.getPublicationDate())
                        .endDate(announcement.getExpirationDate() != null ? announcement.getExpirationDate() : announcement.getPublicationDate().plusDays(1))
                        .eventType(EventType.MEETING)
                        .className("All Classes")
                        .build())
                .collect(Collectors.toList());
    }

    public List<EventReadDto> getEventsByMonthAndClass(int year, int month, Long classId) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = start.plusMonths(1).minusDays(1).atTime(23, 59, 59);
        LocalDate end = endDateTime.toLocalDate(); // Define 'end' for this method too

        SchoolClass schoolClass = schoolClassRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("School class not found: " + classId));
        List<EventReadDto> events = new ArrayList<>();
        events.addAll(mapSchedulesToEvents(scheduleRepository.findByStartTimeBetweenAndSchoolClass(startDateTime, endDateTime, schoolClass)));
        events.addAll(mapHomeworksToEvents(homeworkRepository.findByDueDateBetweenAndSchoolClass(start, end, schoolClass)));
        events.addAll(mapAnnouncementsToEvents(announcementRepository.findByPublicationDateBetween(startDateTime, endDateTime)));
        return events;
    }
}