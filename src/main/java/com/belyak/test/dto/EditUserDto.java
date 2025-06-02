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
public class EditUserDto {

    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String countryCode;
    private String bio;
}
