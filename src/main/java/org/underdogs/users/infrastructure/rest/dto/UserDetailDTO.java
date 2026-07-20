package org.underdogs.users.infrastructure.rest.dto;

import java.time.Instant;
import java.time.LocalDate;
import org.underdogs.users.domain.UserStatus;

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
    UserStatus status,
    String blockedReason,
    Instant createdAt,
    Instant updatedAt) {}
