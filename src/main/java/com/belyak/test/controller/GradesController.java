package com.belyak.test.controller;

import com.belyak.test.dto.CreateGradeDto;
import com.belyak.test.dto.GradeForm;
import com.belyak.test.dto.ReadGradeDto;
import com.belyak.test.exception.EntityNotFoundException;
import com.belyak.test.model.*;
import com.belyak.test.model.enums.Role;
import com.belyak.test.repository.StudentRepository;
import com.belyak.test.repository.SubjectRepository;
import com.belyak.test.repository.TeacherRepository;
import com.belyak.test.service.GradeService;
import com.belyak.test.service.StudentService;
import com.belyak.test.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.belyak.test.model.enums.StatusCodes.ENTITY_NOT_FOUND;

@Controller
@RequiredArgsConstructor
@RequestMapping("/grades")
public class GradesController {

    private final GradeService gradeService;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;

    @GetMapping("/add")
    public String showAddGradeForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = (User) userDetails;
        System.out.println(user);

        // Найти учителя по логину
        Teacher teacher = teacherRepository.findByUser_Username(user.getUsername())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND.name(), "Teacher not found"));

        List<Student> students = studentRepository.findAll();

        List<Subject> availableSubjects = subjectRepository.findAll().stream()
                .filter(subject -> subject.equals(teacher.getSubject()))
                .toList();

        GradeForm form = GradeForm.builder().build();
        form.setTeacherId(teacher.getId()); // автоматически установим teacherId

        model.addAttribute("gradeForm", form);
        model.addAttribute("students", students);
        model.addAttribute("subjects", availableSubjects);

        return "add_grades";
    }


    @PostMapping("/add")
    public String addGrade(@ModelAttribute("gradeForm") GradeForm gradeForm, RedirectAttributes redirectAttributes) {
        try {
            gradeService.addGrade(gradeForm);
            redirectAttributes.addFlashAttribute("success", "Оценка успешно добавлена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при добавлении оценки: " + e.getMessage());
        }
        return "redirect:/grades";
    }

    @PostMapping("/edit/{id}")
    public String updateGrade(@PathVariable Long id,
                              @AuthenticationPrincipal UserDetails userDetails,
                              @ModelAttribute("gradeForm") GradeForm form) {
        User currentUser = (User) userDetails;

        Grade grade = gradeService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grade not found"));

        // Проверка, что текущий пользователь — это учитель, выставивший оценку
        if (!currentUser.getRole().equals(Role.TEACHER) ||
            !grade.getTeacher().getUser().getUsername().equals(currentUser.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to edit this grade");
        }

        // Обновляем только изменяемые поля
        grade.setScore(form.getScore());
        grade.setDate(form.getDate());
        grade.setComment(form.getComment());

        gradeService.save(grade);

        return "redirect:/grades";
    }

    @PostMapping("/delete/{id}")
    public String deleteGrade(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = (User) userDetails;

        Grade grade = gradeService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grade not found"));

        // Удалять может только автор оценки (учитель) или админ
        if (currentUser.getRole().equals(Role.TEACHER)) {
            if (!grade.getTeacher().getUser().getUsername().equals(currentUser.getUsername())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this grade");
            }
        } else if (!currentUser.getRole().equals(Role.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only teachers and admins can delete grades");
        }

        gradeService.deleteById(id);
        return "redirect:/grades";
    }


    @GetMapping
    public String grades(@AuthenticationPrincipal UserDetails userDetails,
                         Model model,
                         @RequestParam(required = false) String subject,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "6") int size) {

        User currentUser = (User) userDetails;
        System.out.println(currentUser.toString());

        List<ReadGradeDto> allGrades;
        if (currentUser.getRole().equals(Role.STUDENT)) {
            allGrades = gradeService.getGradesForStudent(userDetails.getUsername());
        } else if (currentUser.getRole().equals(Role.TEACHER)) {
            allGrades = gradeService.getGradesForTeacher(userDetails.getUsername());
        } else if (currentUser.getRole().equals(Role.PARENT)) {
            allGrades = gradeService.getGradesForParent(userDetails.getUsername());
        } else {
            allGrades = gradeService.getAllGraders();
        }

        if (subject != null && !subject.isEmpty()) {
            allGrades = allGrades.stream()
                    .filter(g -> subject.equals(g.getSubjectMessageKey()))
                    .toList();
        }

        List<ReadGradeDto> sorted = allGrades.stream()
                .sorted(Comparator.comparing(ReadGradeDto::getDate).reversed())
                .toList();

        int start = Math.min(page * size, sorted.size());
        int end = Math.min(start + size, sorted.size());

        List<ReadGradeDto> pagedGrades = sorted.subList(start, end);

        model.addAttribute("grades", pagedGrades);
        model.addAttribute("selectedSubject", subject);
        model.addAttribute("subjects", allGrades.stream().map(ReadGradeDto::getSubjectMessageKey).distinct().sorted().toList());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (int) Math.ceil((double) sorted.size() / size));

        return "grades";
    }


    @GetMapping("/edit/{id}")
    public String editGrade(@PathVariable Long id,
                            @AuthenticationPrincipal UserDetails userDetails,
                            Model model) {
        User currentUser = (User) userDetails;

        // Получаем оценку
        Grade grade = gradeService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grade not found"));

        // Проверка: только учитель может редактировать свои оценки
        if (!currentUser.getRole().equals(Role.TEACHER) ||
            !grade.getTeacher().getUser().getUsername().equals(currentUser.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to edit this grade");
        }

        GradeForm form = GradeForm.builder().build();
        form.setStudentId(grade.getStudent().getId());
        form.setTeacherId(grade.getTeacher().getId());
        form.setSubjectId(grade.getSubject().getId());
        form.setScore(grade.getScore());
        form.setDate(grade.getDate());
        form.setComment(grade.getComment());

        model.addAttribute("gradeForm", form);
        model.addAttribute("editMode", true);
        model.addAttribute("gradeId", grade.getId());

        // Добавляем только одного ученика и один предмет (для select disabled)
        model.addAttribute("students", List.of(grade.getStudent()));
        model.addAttribute("subjects", List.of(grade.getSubject()));

        return "grade_form";
    }

}
