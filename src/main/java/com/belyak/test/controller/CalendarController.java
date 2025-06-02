package com.belyak.test.controller;

import com.belyak.test.dto.EventReadDto;
import com.belyak.test.model.SchoolClass;
import com.belyak.test.model.User;
import com.belyak.test.model.enums.Role;
import com.belyak.test.repository.SchoolClassRepository;
import com.belyak.test.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class CalendarController {

    private final EventService eventService;
    private final SchoolClassRepository schoolClassRepository;

    @GetMapping
    public String getCalendar(
            @RequestParam(required = false) String month,
            @RequestParam(required = false) Long classId,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {

        LocalDate date = month != null ? LocalDate.parse(month) : LocalDate.now();
        List<EventReadDto> events;

        User user = (User) userDetails;
        if (user.getRole() == Role.ADMIN && classId != null) {
            events = eventService.getEventsByMonthAndClass(date.getYear(), date.getMonthValue(), classId);
        } else {
            events = eventService.getEventsByMonth(date.getYear(), date.getMonthValue(), userDetails);
        }

        System.out.println("User Role: " + user.getRole());
        System.out.println("Events fetched: " + events.size());
        events.forEach(event -> System.out.println("Event: " + event.getTitle() + ", Start: " + event.getStartDate()));

        model.addAttribute("events", events);
        model.addAttribute("currentMonth", date);

        if (user.getRole() == Role.ADMIN) {
            List<SchoolClass> classes = schoolClassRepository.findAll();
            model.addAttribute("classes", classes);
        }

        return "calendar";
    }
}