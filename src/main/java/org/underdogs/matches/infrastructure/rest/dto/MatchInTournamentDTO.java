package org.underdogs.matches.infrastructure.rest.dto;

import java.time.Instant;
import org.underdogs.matches.domain.MatchStatus;

public record MatchInTournamentDTO(
    String id,
    String team1Name,
    String team2Name,
    String game,
    Instant scheduledAt,
    MatchStatus status) {}
