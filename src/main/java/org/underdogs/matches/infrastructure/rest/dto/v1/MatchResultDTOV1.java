package org.underdogs.matches.infrastructure.rest.dto.v1;

public record MatchResultDTOV1(
    String matchId,
    String status,
    String team1Id,
    Integer team1Score,
    String team2Id,
    Integer team2Score,
    String winnerTeamId) {}
