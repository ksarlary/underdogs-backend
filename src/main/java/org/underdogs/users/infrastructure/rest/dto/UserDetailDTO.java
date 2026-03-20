package org.underdogs.users.infrastructure.rest.dto;

import java.time.Instant;
import java.time.LocalDate;

public record UserDetailDTO(
        String id,
        String externalAuthId,
        String username,
        String email,
        String firstName,
        String lastName,
        LocalDate birthDate,
        long kibblesBalance,
        String role,
        Instant createdAt,
        Instant updatedAt
) {}