package org.underdogs.players.infrastructure.rest.dto;

public record PlayerDetailDTO(
    String id,
    String nickname,
    String fullName,
    String role,
    String countryCode,
    String teamId,
    String teamName,
    String game) {}
