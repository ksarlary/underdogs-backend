package org.underdogs.matches.application.models;

import java.time.Instant;
import org.underdogs.matches.domain.MatchStatus;
import org.underdogs.teams.domain.Game;

public record UpdateMatchRequest(
    String team1Id,
    String team2Id,
    String tournamentId,
    Game game,
    Instant scheduledAt,
    MatchStatus status,
    Integer team1Score,
    Integer team2Score,
    String winnerTeamId) {}
