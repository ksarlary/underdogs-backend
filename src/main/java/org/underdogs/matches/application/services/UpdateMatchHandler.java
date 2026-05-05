package org.underdogs.matches.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.underdogs.bets.application.usecases.ResolveMatchBets;
import org.underdogs.matches.application.gateways.MatchRepository;
import org.underdogs.matches.application.models.UpdateMatchRequest;
import org.underdogs.matches.application.usecases.UpdateMatch;
import org.underdogs.matches.domain.MatchId;
import org.underdogs.matches.domain.MatchStatus;
import org.underdogs.shared.TimeProvider;
import org.underdogs.shared.error.BusinessErrorCodes;
import org.underdogs.shared.error.BusinessException;
import org.underdogs.teams.application.gateways.TeamRepository;
import org.underdogs.teams.domain.Team;
import org.underdogs.teams.domain.TeamId;
import org.underdogs.tournaments.application.gateways.TournamentRepository;
import org.underdogs.tournaments.domain.Tournament;
import org.underdogs.tournaments.domain.TournamentId;

@Service
class UpdateMatchHandler implements UpdateMatch {

  private final MatchRepository matchRepository;
  private final TeamRepository teamRepository;
  private final TournamentRepository tournamentRepository;
  private final TimeProvider timeProvider;
  private final ResolveMatchBets resolveMatchBets;

  UpdateMatchHandler(
      MatchRepository matchRepository,
      TeamRepository teamRepository,
      TournamentRepository tournamentRepository,
      TimeProvider timeProvider,
      ResolveMatchBets resolveMatchBets) {
    this.matchRepository = matchRepository;
    this.teamRepository = teamRepository;
    this.tournamentRepository = tournamentRepository;
    this.timeProvider = timeProvider;
    this.resolveMatchBets = resolveMatchBets;
  }

  @Override
  @Transactional
  public void handle(MatchId id, UpdateMatchRequest request) {
    final var match =
        matchRepository
            .findById(id)
            .orElseThrow(
                () -> new BusinessException(BusinessErrorCodes.MATCH_NOT_FOUND, "Match not found"));

    Team team1 = null;
    Team team2 = null;
    Tournament tournament = null;
    Team winner = null;

    if (request.team1Id() != null && !request.team1Id().isBlank()) {
      team1 =
          teamRepository
              .findById(new TeamId(request.team1Id()))
              .orElseThrow(
                  () ->
                      new BusinessException(BusinessErrorCodes.TEAM_NOT_FOUND, "Team 1 not found"));
    }

    if (request.team2Id() != null && !request.team2Id().isBlank()) {
      team2 =
          teamRepository
              .findById(new TeamId(request.team2Id()))
              .orElseThrow(
                  () ->
                      new BusinessException(BusinessErrorCodes.TEAM_NOT_FOUND, "Team 2 not found"));
    }

    if (request.tournamentId() != null && !request.tournamentId().isBlank()) {
      tournament =
          tournamentRepository
              .findById(new TournamentId(request.tournamentId()))
              .orElseThrow(
                  () ->
                      new BusinessException(
                          BusinessErrorCodes.TOURNAMENT_NOT_FOUND, "Tournament not found"));
    }

    if (request.winnerTeamId() != null && !request.winnerTeamId().isBlank()) {
      winner =
          teamRepository
              .findById(new TeamId(request.winnerTeamId()))
              .orElseThrow(
                  () ->
                      new BusinessException(
                          BusinessErrorCodes.TEAM_NOT_FOUND, "Winner team not found"));
    }

    MatchStatus statusToApply = request.status();

    if (request.status() == MatchStatus.LIVE) {
      match.startLive(timeProvider.now());
      statusToApply = null;
    }

    match.update(
        team1,
        team2,
        tournament,
        request.game(),
        request.scheduledAt(),
        statusToApply,
        request.team1Score(),
        request.team2Score(),
        winner);

    matchRepository.save(match);

    if (match.getStatus() == MatchStatus.FINISHED || match.getStatus() == MatchStatus.CANCELLED) {
      resolveMatchBets.handle(match);
    }
  }
}
