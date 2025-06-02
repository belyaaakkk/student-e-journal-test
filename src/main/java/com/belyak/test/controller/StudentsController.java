package com.belyak.test.controller;

import com.belyak.test.dto.CreateStudentDto;
import com.belyak.test.dto.EditStudentDto;
import com.belyak.test.dto.ReadStudentDto;
import com.belyak.test.service.CountryService;
import com.belyak.test.service.SchoolClassService;
import com.belyak.test.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/students")
@PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
public class StudentsController {

    private final StudentService studentService;
    private final SchoolClassService schoolClassService;
    private final CountryService countryService;

    @GetMapping
    public String students(Model model,
                           @RequestParam(value = "sortField", defaultValue = "user.lastname") String sortField,
                           @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
                           @RequestParam(value = "classFilter", required = false) String classFilter,
                           @RequestParam(value = "page", defaultValue = "0") int page) {

        Sort sort = Sort.by(sortDirection.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortField);
        Pageable pageable = PageRequest.of(page, 15, sort); // 15 студентов на страницу

        Page<ReadStudentDto> studentsPage = studentService.getAllStudentsFiltered(classFilter, pageable);

        model.addAttribute("studentsPage", studentsPage);
        model.addAttribute("students", studentsPage.getContent());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("classFilter", classFilter);
        model.addAttribute("allClasses", schoolClassService.getAllClassNames());

        return "students";
    }

    @GetMapping("/{studentId}")
    public String student(@PathVariable("studentId") Long studentId, Model model) {
        model.addAttribute("student", this.studentService.getStudentById(studentId));
        return "student";
    }

    @GetMapping("/{studentId}/edit")
    public String editStudent(@PathVariable("studentId") Long studentId, Model model) {
        ReadStudentDto student = studentService.getStudentById(studentId);
        model.addAttribute("student", student);
        model.addAttribute("classNames", schoolClassService.getAllClassNames());
        return "edit-student";
    }

    @PostMapping("/{studentId}/edit")
    public String editStudent(@PathVariable Long studentId,
                              @Valid @ModelAttribute EditStudentDto userDto,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("errors", result.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList()));
            model.addAttribute("student", studentService.getStudentById(studentId));
            return "edit-student";
        }
        studentService.updateStudent(studentId, userDto);
        return "redirect:/students/" + studentId;
    }

    @GetMapping("/create")
    public String showCreateStudentForm(Model model) {
        model.addAttribute("student", new CreateStudentDto("", "", "", "", "",
                LocalDate.now(), LocalDate.now(), "", "", ""));
        model.addAttribute("countries", countryService.findAll());
        model.addAttribute("classNames", schoolClassService.getAllClassNames());
        return "create-student";
    }

    @PostMapping("/create")
    public String createStudent(@Valid @ModelAttribute CreateStudentDto student, Model model) {
        studentService.createStudent(student);
        return "redirect:/students";
    }

    @PostMapping("/{id}/delete")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudentById(id);
        return "redirect:/students";
    }
}
