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
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class CalendarController {

    private final EventService eventService;

    @GetMapping
    public String getCalendar(
            @RequestParam(required = false) String month,
            @RequestParam(required = false) Long classId,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = (User) userDetails;
        LocalDate date = (month != null) ? LocalDate.parse(month) : LocalDate.now();

        List<EventReadDto> scheduleEvents = eventService.getEventsByMonth(date.getYear(), date.getMonthValue(), user);
        List<EventReadDto> homeworkEvents = eventService.getHomeworkEventsByMonth(date.getYear(), date.getMonthValue(), user);

        List<EventReadDto> combinedEvents = new ArrayList<>();
        combinedEvents.addAll(scheduleEvents);
        combinedEvents.addAll(homeworkEvents);

        model.addAttribute("events", combinedEvents);
        model.addAttribute("currentMonth", date);

        return "calendar";
    }


}