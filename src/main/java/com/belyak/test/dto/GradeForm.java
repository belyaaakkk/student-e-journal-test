package com.belyak.test.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class GradeForm {
    private Long studentId;
    private Long teacherId;
    private Long subjectId;
    private Integer score;
    private LocalDate date;
    private String comment;
}
