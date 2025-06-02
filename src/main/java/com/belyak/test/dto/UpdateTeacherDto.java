package com.belyak.test.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTeacherDto {
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String subjectCode;
    private String bio;
}
