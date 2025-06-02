package com.belyak.test.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkReadDto {
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private String status;
    private String subjectName;
    private String className;
    private String teacherFullName;
}
