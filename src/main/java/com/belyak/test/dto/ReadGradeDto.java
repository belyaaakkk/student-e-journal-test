package com.belyak.test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadGradeDto {

    private Long id;
    private ReadStudentDto student;
    private String subjectMessageKey;
    private ReadTeacherDto teacher;
    private int score;
    private LocalDate date;
    private String comment;
}
