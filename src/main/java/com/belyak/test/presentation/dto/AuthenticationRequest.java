package com.belyak.test.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    @Schema(example = "user@example.com", description = "Email used for login")
    @NotBlank
    @Email
    private String email;

    @Schema(example = "Password123!", description = "Password")
    @NotBlank
    @Size(min = 8, max = 64)
    private String password;
}
