package org.underdogs.matches.application.models;

import java.time.LocalDateTime;
import org.underdogs.matches.domain.MatchStatus;
import org.underdogs.teams.domain.Game;

public record UpdateMatchRequest(
    String team1Id,
    String team2Id,
    String tournamentId,
    Game game,
    LocalDateTime scheduledAt,
    MatchStatus status,
    Integer team1Score,
    Integer team2Score,
    String winnerTeamId) {}
