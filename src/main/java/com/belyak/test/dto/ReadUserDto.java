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
public class ReadUserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String bio;
    private String photoUrl;
    private String role; // Например, "student", "admin"
    private String countryCode; // Например, "label.country.ukraine"
    private boolean accountStatus;
}