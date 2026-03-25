package org.underdogs.matches.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.underdogs.matches.application.gateways.MatchRepository;
import org.underdogs.matches.application.models.CreateMatchRequest;
import org.underdogs.matches.application.usecases.CreateMatch;
import org.underdogs.matches.domain.Match;
import org.underdogs.matches.domain.MatchId;
import org.underdogs.shared.DomainIdGenerator;
import org.underdogs.shared.error.BusinessErrorCodes;
import org.underdogs.shared.error.BusinessException;
import org.underdogs.teams.application.gateways.TeamRepository;
import org.underdogs.teams.domain.TeamId;
import org.underdogs.tournaments.application.gateways.TournamentRepository;
import org.underdogs.tournaments.domain.TournamentId;

@Service
class CreateMatchHandler implements CreateMatch {

  private final MatchRepository matchRepository;
  private final TeamRepository teamRepository;
  private final TournamentRepository tournamentRepository;
  private final DomainIdGenerator domainIdGenerator;

  CreateMatchHandler(
      MatchRepository matchRepository,
      TeamRepository teamRepository,
      TournamentRepository tournamentRepository,
      DomainIdGenerator domainIdGenerator) {
    this.matchRepository = matchRepository;
    this.teamRepository = teamRepository;
    this.tournamentRepository = tournamentRepository;
    this.domainIdGenerator = domainIdGenerator;
  }

  @Override
  @Transactional
  public MatchId handle(CreateMatchRequest request) {
    final var team1 =
        teamRepository
            .findById(new TeamId(request.team1Id()))
            .orElseThrow(
                () -> new BusinessException(BusinessErrorCodes.TEAM_NOT_FOUND, "Team 1 not found"));

    final var team2 =
        teamRepository
            .findById(new TeamId(request.team2Id()))
            .orElseThrow(
                () -> new BusinessException(BusinessErrorCodes.TEAM_NOT_FOUND, "Team 2 not found"));

    final var tournament =
        tournamentRepository
            .findById(new TournamentId(request.tournamentId()))
            .orElseThrow(
                () ->
                    new BusinessException(
                        BusinessErrorCodes.TOURNAMENT_NOT_FOUND, "Tournament not found"));

    final MatchId matchId = new MatchId(domainIdGenerator.generate());

    final Match match =
        Match.create(matchId, team1, team2, tournament, request.game(), request.scheduledAt());

    matchRepository.save(match);
    return matchId;
  }
}
