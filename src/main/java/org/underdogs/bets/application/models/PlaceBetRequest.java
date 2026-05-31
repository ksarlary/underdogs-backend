package org.underdogs.bets.application.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PlaceBetRequest(
    @NotBlank String matchId, @NotBlank String teamId, @Min(1) long amount) {}
