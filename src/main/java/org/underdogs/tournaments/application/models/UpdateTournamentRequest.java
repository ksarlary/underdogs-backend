package org.underdogs.tournaments.application.models;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.underdogs.teams.domain.Game;

public record UpdateTournamentRequest(
    @Size(min = 2, max = 100) String name, Game game, LocalDate startDate, LocalDate endDate) {}
