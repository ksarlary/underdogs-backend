package org.underdogs.matches.infrastructure.web.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.underdogs.matches.application.usecases.GetMatchResult;
import org.underdogs.matches.domain.Match;
import org.underdogs.matches.infrastructure.rest.dto.v1.MatchResultDTOV1;

@RestController
@RequestMapping("/api/v1/matches")
public class MatchResultControllerV1 {

  private final GetMatchResult getMatchResult;

  public MatchResultControllerV1(GetMatchResult getMatchResult) {
    this.getMatchResult = getMatchResult;
  }

  @GetMapping("/{id}/result")
  public MatchResultDTOV1 getResult(@PathVariable String id) {
    Match match = getMatchResult.handle(id);

    return new MatchResultDTOV1(
        match.getId().value(),
        match.getStatus().name(),
        match.getTeam1().getId().value(),
        match.getTeam1Score(),
        match.getTeam2().getId().value(),
        match.getTeam2Score(),
        match.getWinner() == null ? null : match.getWinner().getId().value());
  }
}
