package org.underdogs.matches.infrastructure.rest.v2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.underdogs.matches.application.usecases.GetMatchResult;
import org.underdogs.matches.domain.Match;
import org.underdogs.matches.infrastructure.rest.dto.v2.MatchResultDTOV2;

@RestController
@RequestMapping("/api/v2/matches")
public class MatchResultControllerV2 {

  private final GetMatchResult getMatchResult;

  public MatchResultControllerV2(GetMatchResult getMatchResult) {
    this.getMatchResult = getMatchResult;
  }

  @GetMapping("/{id}/result")
  public MatchResultDTOV2 getResult(@PathVariable String id) {
    Match match = getMatchResult.handle(id);

    MatchResultDTOV2.MatchInfo matchInfo =
        new MatchResultDTOV2.MatchInfo(match.getId().value(), match.getStatus().name());

    MatchResultDTOV2.TeamResult home =
        new MatchResultDTOV2.TeamResult(
            match.getTeam1().getId().value(), match.getTeam1().getName(), match.getTeam1Score());

    MatchResultDTOV2.TeamResult away =
        new MatchResultDTOV2.TeamResult(
            match.getTeam2().getId().value(), match.getTeam2().getName(), match.getTeam2Score());

    MatchResultDTOV2.TeamsInfo teams = new MatchResultDTOV2.TeamsInfo(home, away);

    MatchResultDTOV2.WinnerInfo winner = null;

    if (match.getWinner() != null) {
      winner =
          new MatchResultDTOV2.WinnerInfo(
              match.getWinner().getId().value(), match.getWinner().getName());
    }

    return new MatchResultDTOV2(matchInfo, teams, winner);
  }
}
