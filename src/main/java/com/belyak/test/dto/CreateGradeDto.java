package com.belyak.test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGradeDto {
    private Long studentId;
    private String subjectCode;
    private int score;
    private String comment;
}
