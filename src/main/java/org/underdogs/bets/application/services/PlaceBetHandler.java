package org.underdogs.bets.application.services;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.underdogs.bets.application.gateways.BetRepository;
import org.underdogs.bets.application.models.PlaceBetRequest;
import org.underdogs.bets.application.usecases.PlaceBet;
import org.underdogs.bets.domain.Bet;
import org.underdogs.bets.domain.BetId;
import org.underdogs.matches.application.gateways.MatchRepository;
import org.underdogs.matches.domain.MatchId;
import org.underdogs.shared.DomainIdGenerator;
import org.underdogs.shared.TimeProvider;
import org.underdogs.shared.error.BusinessErrorCodes;
import org.underdogs.shared.error.BusinessException;
import org.underdogs.teams.application.gateways.TeamRepository;
import org.underdogs.teams.domain.TeamId;
import org.underdogs.users.application.gateways.UserRepository;
import org.underdogs.users.application.usecases.SyncCurrentUser;

@Service
class PlaceBetHandler implements PlaceBet {

  private final BetRepository betRepository;
  private final MatchRepository matchRepository;
  private final TeamRepository teamRepository;
  private final UserRepository userRepository;
  private final SyncCurrentUser syncCurrentUser;
  private final DomainIdGenerator domainIdGenerator;
  private final TimeProvider timeProvider;
  private final BetCoefficientCalculator coefficientCalculator;

  PlaceBetHandler(
      BetRepository betRepository,
      MatchRepository matchRepository,
      TeamRepository teamRepository,
      UserRepository userRepository,
      SyncCurrentUser syncCurrentUser,
      DomainIdGenerator domainIdGenerator,
      TimeProvider timeProvider,
      BetCoefficientCalculator coefficientCalculator) {
    this.betRepository = betRepository;
    this.matchRepository = matchRepository;
    this.teamRepository = teamRepository;
    this.userRepository = userRepository;
    this.syncCurrentUser = syncCurrentUser;
    this.domainIdGenerator = domainIdGenerator;
    this.timeProvider = timeProvider;
    this.coefficientCalculator = coefficientCalculator;
  }

  @Override
  @Transactional
  public BetId handle(Jwt jwt, PlaceBetRequest request) {
    final var user = syncCurrentUser.handle(jwt);

    final var match =
        matchRepository
            .findById(new MatchId(request.matchId()))
            .orElseThrow(
                () -> new BusinessException(BusinessErrorCodes.MATCH_NOT_FOUND, "Match not found"));

    if (!match.isOpenForBets(timeProvider.now())) {
      throw new BusinessException(
          BusinessErrorCodes.MATCH_NOT_OPEN_FOR_BETS, "This match is not open for bets");
    }

    final var selectedTeam =
        teamRepository
            .findById(new TeamId(request.teamId()))
            .orElseThrow(
                () ->
                    new BusinessException(
                        BusinessErrorCodes.TEAM_NOT_FOUND, "Selected team not found"));

    if (!match.containsTeam(selectedTeam)) {
      throw new BusinessException(
          BusinessErrorCodes.TEAM_NOT_IN_MATCH, "Selected team is not part of this match");
    }

    if (betRepository.existsByUserAndMatch(user, match)) {
      throw new BusinessException(
          BusinessErrorCodes.BET_ALREADY_EXISTS, "You already placed a bet on this match");
    }

    long totalPool = betRepository.sumAmountByMatch(match);
    long selectedTeamPool = betRepository.sumAmountByMatchAndSelectedTeam(match, selectedTeam);

    double coefficient = coefficientCalculator.calculate(totalPool, selectedTeamPool);
    long potentialGain =
        coefficientCalculator.calculatePotentialGain(request.amount(), coefficient);

    user.debitKibbles(request.amount());

    final var betId = new BetId(domainIdGenerator.generate());

    final var bet =
        Bet.create(
            betId,
            user,
            match,
            selectedTeam,
            request.amount(),
            coefficient,
            potentialGain,
            timeProvider.now());

    userRepository.save(user);
    betRepository.save(bet);

    return betId;
  }
}
