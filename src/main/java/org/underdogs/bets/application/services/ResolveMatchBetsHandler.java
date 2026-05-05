package org.underdogs.bets.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.underdogs.bets.application.gateways.BetRepository;
import org.underdogs.bets.application.usecases.ResolveMatchBets;
import org.underdogs.matches.domain.Match;
import org.underdogs.matches.domain.MatchStatus;
import org.underdogs.shared.TimeProvider;
import org.underdogs.shared.error.BusinessErrorCodes;
import org.underdogs.shared.error.BusinessException;
import org.underdogs.users.application.gateways.UserRepository;

@Service
class ResolveMatchBetsHandler implements ResolveMatchBets {

  private final BetRepository betRepository;
  private final UserRepository userRepository;
  private final TimeProvider timeProvider;

  ResolveMatchBetsHandler(
      BetRepository betRepository, UserRepository userRepository, TimeProvider timeProvider) {
    this.betRepository = betRepository;
    this.userRepository = userRepository;
    this.timeProvider = timeProvider;
  }

  @Override
  @Transactional
  public void handle(Match match) {
    if (match.getStatus() == MatchStatus.CANCELLED) {
      cancelAndRefundBets(match);
      return;
    }

    if (match.getStatus() != MatchStatus.FINISHED) {
      return;
    }

    if (match.getWinner() == null) {
      throw new BusinessException(
          BusinessErrorCodes.MATCH_WINNER_REQUIRED,
          "A finished match must have a winner to resolve bets");
    }

    resolveFinishedMatchBets(match);
  }

  private void resolveFinishedMatchBets(Match match) {
    final var now = timeProvider.now();
    final var bets = betRepository.findByMatch(match);

    for (final var bet : bets) {
      if (bet.getSelectedTeam().getId().equals(match.getWinner().getId())) {
        bet.markWon(now);
        bet.getUser().creditKibbles(bet.getPotentialGain());
        userRepository.save(bet.getUser());
      } else {
        bet.markLost(now);
      }

      betRepository.save(bet);
    }
  }

  private void cancelAndRefundBets(Match match) {
    final var now = timeProvider.now();
    final var bets = betRepository.findByMatch(match);

    for (final var bet : bets) {
      bet.cancel(now);
      bet.getUser().creditKibbles(bet.getAmount());

      userRepository.save(bet.getUser());
      betRepository.save(bet);
    }
  }
}
