package org.underdogs.matches.infrastructure.rest.dto;

import java.time.LocalDateTime;
import org.underdogs.matches.domain.MatchStatus;

public record MatchInTournamentDTO(
    String id,
    String team1Name,
    String team2Name,
    String game,
    LocalDateTime scheduledAt,
    MatchStatus status) {}
