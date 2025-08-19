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
public class RegisterRequest {

    @Schema(example = "john_doe", description = "Unique username")
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @Schema(example = "user@example.com", description = "Valid email address")
    @NotBlank
    @Email
    private String email;

    @Schema(example = "Password123!", description = "Password with uppercase, lowercase, digit and special char")
    @NotBlank
    @Size(min = 8, max = 64)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).+$",
            message = "Password must contain at least 1 uppercase, 1 lowercase, 1 digit, and 1 special character"
    )
    private String password;
}
