package org.underdogs.tournaments.infrastructure.rest.dto;

import java.time.LocalDate;
import java.util.List;
import org.underdogs.matches.infrastructure.rest.dto.MatchInTournamentDTO;

public record TournamentDetailDTO(
    String id,
    String name,
    String game,
    LocalDate startDate,
    LocalDate endDate,
    List<MatchInTournamentDTO> matches) {}
