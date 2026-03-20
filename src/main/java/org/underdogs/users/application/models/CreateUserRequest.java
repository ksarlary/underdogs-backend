package org.underdogs.users.application.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.underdogs.users.application.validation.Adult;

import java.time.LocalDate;
import java.util.Date;

public record CreateUserRequest(
        @NotBlank
        String externalAuthId,

        @NotBlank
        @Size(min = 3, max = 50)
        String username,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 2, max = 40)
        String firstName,

        @NotBlank
        @Size(min = 2, max = 40)
        String lastName,

        @Adult(min = 18)
        LocalDate birthDate
) {}
