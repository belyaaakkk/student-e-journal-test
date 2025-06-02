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
public class CreateTeacherDto {
    private String firstname;
    private String lastname;
    private String email;
    private String username;
    private String subjectCode;
    private String countryCode;
    private String bio;
    private LocalDate dateOfBirth;
    private String phoneNumber;
}
