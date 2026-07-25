package org.underdogs.bets.application.services;

import java.util.Map;
import org.springframework.stereotype.Service;
import org.underdogs.bets.application.gateways.BetRepository;
import org.underdogs.bets.application.usecases.GetBetStats;
import org.underdogs.bets.domain.BetStatus;

@Service
class GetBetStatsHandler implements GetBetStats {

  private final BetRepository betRepository;

  GetBetStatsHandler(BetRepository betRepository) {
    this.betRepository = betRepository;
  }

  @Override
  public Map<BetStatus, Long> handle() {
    return betRepository.countByStatus();
  }
}
