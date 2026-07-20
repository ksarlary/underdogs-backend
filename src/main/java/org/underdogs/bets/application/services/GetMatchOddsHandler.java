package org.underdogs.bets.application.services;

import org.springframework.stereotype.Service;
import org.underdogs.bets.application.gateways.BetRepository;
import org.underdogs.bets.application.models.MatchOddsResponse;
import org.underdogs.bets.application.models.TeamOddsResponse;
import org.underdogs.bets.application.usecases.GetMatchOdds;
import org.underdogs.matches.application.gateways.MatchRepository;
import org.underdogs.matches.domain.MatchId;
import org.underdogs.shared.error.BusinessErrorCodes;
import org.underdogs.shared.error.BusinessException;
import org.underdogs.teams.domain.Team;

@Service
class GetMatchOddsHandler implements GetMatchOdds {

  private final MatchRepository matchRepository;
  private final BetRepository betRepository;
  private final BetCoefficientCalculator coefficientCalculator;

  GetMatchOddsHandler(
      MatchRepository matchRepository,
      BetRepository betRepository,
      BetCoefficientCalculator coefficientCalculator) {
    this.matchRepository = matchRepository;
    this.betRepository = betRepository;
    this.coefficientCalculator = coefficientCalculator;
  }

  @Override
  public MatchOddsResponse handle(MatchId matchId) {
    final var match =
        matchRepository
            .findById(matchId)
            .orElseThrow(
                () -> new BusinessException(BusinessErrorCodes.MATCH_NOT_FOUND, "Match not found"));

    final long totalPool = betRepository.sumAmountByMatch(match);

    final var team1 = match.getTeam1();
    final var team2 = match.getTeam2();

    return new MatchOddsResponse(
        match.getId().value(),
        buildTeamOdds(match, team1, totalPool),
        buildTeamOdds(match, team2, totalPool));
  }

  private TeamOddsResponse buildTeamOdds(
      org.underdogs.matches.domain.Match match, Team team, long totalPool) {
    final long teamPool = betRepository.sumAmountByMatchAndSelectedTeam(match, team);
    final double coefficient = coefficientCalculator.calculate(totalPool, teamPool);

    return new TeamOddsResponse(team.getId().value(), team.getName(), coefficient);
  }
}
