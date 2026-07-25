package org.underdogs.matches.infrastructure.rest.dto;

import java.time.Instant;
import org.underdogs.matches.domain.MatchStatus;

public record MatchDetailDTO(
    String id,
    String team1Id,
    String team1Name,
    Integer team1Score,
    String team2Id,
    String team2Name,
    Integer team2Score,
    String tournamentId,
    String tournamentName,
    String game,
    Instant scheduledAt,
    MatchStatus status,
    String winnerTeamId,
    String winnerTeamName,
    boolean bettingOpen,
    Instant bettingClosesAt) {}
