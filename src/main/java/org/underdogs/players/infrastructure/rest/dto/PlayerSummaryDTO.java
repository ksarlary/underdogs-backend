package org.underdogs.players.infrastructure.rest.dto;

public record PlayerSummaryDTO(
    String id,
    String nickname,
    String fullName,
    String role,
    String countryCode,
    String teamId,
    String teamName) {}
