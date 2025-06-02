package com.belyak.test.controller;

import com.belyak.test.dto.CreateTeacherDto;
import com.belyak.test.dto.ReadTeacherDto;
import com.belyak.test.dto.UpdateTeacherDto;
import com.belyak.test.service.CountryService;
import com.belyak.test.service.SubjectService;
import com.belyak.test.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeachersController {

    private final TeacherService teacherService;
    private final SubjectService subjectService;
    private final CountryService countryService;


    @GetMapping
    public String teachers(Model model) {
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "teachers";
    }

    @GetMapping("/{teacherId}")
    public String teacherById(@PathVariable Long teacherId, Model model) {
        model.addAttribute("teacher", teacherService.getTeacherById(teacherId));
        System.out.println(teacherService.getTeacherById(teacherId));
        return "teacher";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        ReadTeacherDto teacher = teacherService.getTeacherById(id);
        model.addAttribute("subjects", subjectService.getAllSubjects());
        model.addAttribute("teacher", teacher);
        return "edit-teacher";
    }

    @PostMapping("/{teacherId}/edit")
    public String editTeacher(@PathVariable Long teacherId, @ModelAttribute UpdateTeacherDto dto) {
        teacherService.updateTeacher(teacherId, dto);
        return "redirect:/teachers/" + teacherId;
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("teacher", new CreateTeacherDto("", "", "", "", "", "", "", LocalDate.now(), ""));
        model.addAttribute("subjects", subjectService.getAllSubjects());
        model.addAttribute("countries", countryService.findAll());
        return "create-teacher";
    }

    @PostMapping("/create")
    public String createTeacher(@ModelAttribute("teacher") @Valid CreateTeacherDto teacher,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("subjects", subjectService.getAllSubjects());
            model.addAttribute("countries", countryService.findAll());
            return "create-teacher";
        }

        teacherService.createTeacher(teacher);
        return "redirect:/teachers";
    }

    @PostMapping("/{id}/delete")
    public String deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacherById(id);
        return "redirect:/teachers";
    }
}
