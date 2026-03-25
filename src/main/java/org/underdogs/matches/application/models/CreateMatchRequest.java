package org.underdogs.matches.application.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.underdogs.teams.domain.Game;

public record CreateMatchRequest(
    @NotBlank String team1Id,
    @NotBlank String team2Id,
    @NotBlank String tournamentId,
    @NotNull Game game,
    @NotNull LocalDateTime scheduledAt) {}
