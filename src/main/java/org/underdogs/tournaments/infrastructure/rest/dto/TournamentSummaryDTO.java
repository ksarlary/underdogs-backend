package org.underdogs.tournaments.infrastructure.rest.dto;

import java.time.LocalDate;

public record TournamentSummaryDTO(
    String id, String name, String game, LocalDate startDate, LocalDate endDate) {}
