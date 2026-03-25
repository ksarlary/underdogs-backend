package org.underdogs.tournaments.infrastructure.rest.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import org.underdogs.matches.infrastructure.rest.dto.MatchInTournamentDTO;
import org.underdogs.tournaments.domain.Tournament;
import org.underdogs.tournaments.infrastructure.rest.dto.TournamentDetailDTO;
import org.underdogs.tournaments.infrastructure.rest.dto.TournamentSummaryDTO;

@Component
public class TournamentMapper {

  public TournamentSummaryDTO toSummaryDTO(Tournament tournament) {
    return new TournamentSummaryDTO(
        tournament.getId().value(), tournament.getName(), tournament.getGame().name());
  }

  public TournamentDetailDTO toDetailDTO(Tournament tournament) {
    return new TournamentDetailDTO(
        tournament.getId().value(),
        tournament.getName(),
        tournament.getGame().name(),
        tournament.getStartDate(),
        tournament.getEndDate(),
        tournament.getMatches().stream()
            .map(
                match ->
                    new MatchInTournamentDTO(
                        match.getId().value(),
                        match.getTeam1().getName(),
                        match.getTeam2().getName(),
                        match.getGame().name(),
                        match.getScheduledAt(),
                        match.getStatus()))
            .toList());
  }

  public List<TournamentSummaryDTO> toSummaryDTOList(List<Tournament> tournaments) {
    return tournaments.stream().map(this::toSummaryDTO).toList();
  }
}
