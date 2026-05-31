package org.underdogs.users.infrastructure.rest.dto;

import java.time.Instant;
import org.underdogs.users.domain.UserStatus;

public record UserSummaryDTO(
    String id,
    String username,
    long kibblesBalance,
    String role,
    Instant createdAt,
    UserStatus status,
    String blockedReason) {}
