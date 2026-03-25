package org.underdogs.tournaments.application.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.underdogs.teams.domain.Game;

public record CreateTournamentRequest(
    @NotBlank @Size(min = 2, max = 100) String name,
    @NotNull Game game,
    @NotNull LocalDate startDate,
    @NotNull LocalDate endDate) {}
