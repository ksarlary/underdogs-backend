package org.underdogs.teams.application.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.underdogs.teams.domain.Game;

public record CreateTeamRequest(
    @NotBlank @Size(min = 2, max = 80) String name,
    @NotBlank @Size(min = 2, max = 10) String tag,
    @NotNull Game game) {}
