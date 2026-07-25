package org.underdogs.bets.infrastructure.rest.dto;

import java.time.Instant;
import org.underdogs.bets.domain.BetStatus;

public record BetDTO(
    String id,
    String matchId,
    String team1Name,
    String team2Name,
    String selectedTeamId,
    String selectedTeamName,
    String username,
    long amount,
    double coefficient,
    long potentialGain,
    BetStatus status,
    Instant createdAt,
    Instant resolvedAt) {}
