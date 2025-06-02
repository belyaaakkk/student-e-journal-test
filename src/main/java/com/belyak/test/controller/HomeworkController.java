package com.belyak.test.controller;

import com.belyak.test.dto.HomeworkCreateDto;
import com.belyak.test.dto.HomeworkReadDto;
import com.belyak.test.exception.EntityNotFoundException;
import com.belyak.test.model.enums.HomeworkStatus;
import com.belyak.test.service.HomeworkService;
import com.belyak.test.service.SchoolClassService;
import com.belyak.test.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/homework")
public class HomeworkController {

    private final HomeworkService homeworkService;
    private final SubjectService subjectService;
    private final SchoolClassService schoolClassService;

    @GetMapping
    public String getHomework(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestParam(required = false) HomeworkStatus status,
                              Model model) {
        List<HomeworkReadDto> homeworkList;

        try {
            homeworkList = homeworkService.getHomeworkForStudent(userDetails.getUsername(), status);
        } catch (EntityNotFoundException ex1) {
            try {
                homeworkList = homeworkService.getHomeworkForTeacher(userDetails.getUsername(), status);
            } catch (EntityNotFoundException ex2) {
                homeworkList = homeworkService.getHomeworkForParent(userDetails.getUsername(), status);
            }
        }

        model.addAttribute("homeworkList", homeworkList);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", HomeworkStatus.values());

        return "homework";
    }


    @GetMapping("/add")
    public String addHomework(Model model) {
        HomeworkCreateDto dto = new HomeworkCreateDto(
                "", "", LocalDateTime.now().plusDays(1),
                "", "", "");
        model.addAttribute("homework", dto);
        model.addAttribute("subjects", subjectService.getAllSubjects());
        model.addAttribute("classes", schoolClassService.getAllClassNames());
        return "add_homework";
    }

    @PostMapping("/add")
    public String addHomework(@ModelAttribute("homework") HomeworkCreateDto dto,
                              @AuthenticationPrincipal UserDetails userDetails) {
        System.out.println(dto);
        homeworkService.createHomework(dto, userDetails.getUsername());
        return "redirect:/homework";
    }

    @PostMapping("/delete/{id}")
    public String deleteHomework(@PathVariable("id") Long id,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        homeworkService.deleteHomework(id, userDetails.getUsername());
        return "redirect:/homework";
    }
}
