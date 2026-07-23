package org.underdogs.matches.infrastructure.rest.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import org.underdogs.matches.domain.Match;
import org.underdogs.matches.infrastructure.rest.dto.MatchDetailDTO;
import org.underdogs.matches.infrastructure.rest.dto.MatchSummaryDTO;
import org.underdogs.shared.TimeProvider;

@Component
public class MatchMapper {

  private final TimeProvider timeProvider;

  public MatchMapper(TimeProvider timeProvider) {
    this.timeProvider = timeProvider;
  }

  public MatchSummaryDTO toSummaryDTO(Match match) {
    return new MatchSummaryDTO(
        match.getId().value(),
        match.getTeam1().getName(),
        match.getTeam2().getName(),
        match.getGame().name(),
        match.getScheduledAt(),
        match.getStatus());
  }

  public MatchDetailDTO toDetailDTO(Match match) {
    return new MatchDetailDTO(
        match.getId().value(),
        match.getTeam1().getId().value(),
        match.getTeam1().getName(),
        match.getTeam1Score(),
        match.getTeam2().getId().value(),
        match.getTeam2().getName(),
        match.getTeam2Score(),
        match.getTournament().getId().value(),
        match.getTournament().getName(),
        match.getGame().name(),
        match.getScheduledAt(),
        match.getStatus(),
        match.getWinner() != null ? match.getWinner().getId().value() : null,
        match.getWinner() != null ? match.getWinner().getName() : null,
        match.isOpenForBets(timeProvider.now()),
        match.getBettingClosesAt());
  }

  public List<MatchSummaryDTO> toSummaryDTOList(List<Match> matches) {
    return matches.stream().map(this::toSummaryDTO).toList();
  }
}
