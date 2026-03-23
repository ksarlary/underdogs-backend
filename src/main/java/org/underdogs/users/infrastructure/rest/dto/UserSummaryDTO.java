package org.underdogs.users.infrastructure.rest.dto;

import java.time.Instant;

public record UserSummaryDTO(
    String id, String username, long kibblesBalance, String role, Instant createdAt) {}
