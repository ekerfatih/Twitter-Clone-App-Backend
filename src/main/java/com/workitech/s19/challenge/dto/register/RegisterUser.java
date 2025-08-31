package com.workitech.s19.challenge.dto.register;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUser(
        @Size(max = 20)
        @NotBlank
        String username,
        @Email
        @Size(max = 40)
        @NotBlank
        String email,
        @Size(max = 100)
        @NotBlank
        String password,
        @Size(max = 20)
        @NotBlank
        String firstName,
        @Size(max = 20)
        @NotBlank
        String lastName
) {
}
