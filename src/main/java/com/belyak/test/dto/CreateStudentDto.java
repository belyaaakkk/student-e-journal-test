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
public class CreateStudentDto {
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private LocalDate enrollmentDate;
    private String countryCode;
    private String classCode;
    private String bio;
}
