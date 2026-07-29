package org.underdogs.bets.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.underdogs.bets.application.gateways.BetRepository;
import org.underdogs.bets.domain.BetId;
import org.underdogs.bets.dtos.ResolveEventBetsResponse;
import org.underdogs.shared.TimeProvider;
import org.underdogs.users.application.gateways.UserRepository;

@Service
public class BetResolutionService {

  private static final Logger logger = LoggerFactory.getLogger(BetResolutionService.class);

  private final BetRepository betRepository;
  private final UserRepository userRepository;
  private final TimeProvider timeProvider;

  public BetResolutionService(
      BetRepository betRepository, UserRepository userRepository, TimeProvider timeProvider) {
    this.betRepository = betRepository;
    this.userRepository = userRepository;
    this.timeProvider = timeProvider;
  }

  @Transactional
  public void processBetResolution(ResolveEventBetsResponse response) {
    if (!"SUCCESS".equals(response.status())) {
      logger.error(
          "Worker reported a failure for event {}: {}",
          response.eventId(),
          response.errorMessage());
      return;
    }

    final var now = timeProvider.now();
    int applied = 0;

    for (final var result : response.results()) {
      if (result.betId() == null || result.betId().isBlank()) {
        logger.warn("Resolution of event {} carries a result without bet id", response.eventId());
        continue;
      }

      final var bet = betRepository.findById(new BetId(result.betId())).orElse(null);

      if (bet == null) {
        logger.warn(
            "Bet {} is unknown, skipped in the resolution of event {}",
            result.betId(),
            response.eventId());
        continue;
      }

      switch (result.status()) {
        case "WON" -> {
          bet.markWon(now);
          bet.getUser().creditKibbles(result.kibblesToCredit());
          userRepository.save(bet.getUser());
          betRepository.save(bet);
          applied++;
        }
        case "LOST" -> {
          bet.markLost(now);
          betRepository.save(bet);
          applied++;
        }
        case "CANCELLED" -> {
          bet.cancel(now);
          bet.getUser().creditKibbles(bet.getAmount());
          userRepository.save(bet.getUser());
          betRepository.save(bet);
          applied++;
        }
        default -> logger.warn("Unknown outcome '{}' for bet {}", result.status(), result.betId());
      }
    }

    logger.info(
        "Resolution of event {} applied to {} of {} bets",
        response.eventId(),
        applied,
        response.results().size());
  }
}
