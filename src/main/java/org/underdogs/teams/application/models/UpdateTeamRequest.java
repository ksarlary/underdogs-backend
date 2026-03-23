package org.underdogs.teams.application.models;

import jakarta.validation.constraints.Size;
import org.underdogs.teams.domain.Game;

public record UpdateTeamRequest(
    @Size(min = 2, max = 80) String name, @Size(min = 2, max = 10) String tag, Game game) {}
