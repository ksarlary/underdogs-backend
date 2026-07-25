package org.underdogs.bets.application.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.underdogs.bets.application.gateways.BetRepository;
import org.underdogs.bets.application.usecases.SearchBets;
import org.underdogs.bets.domain.Bet;
import org.underdogs.bets.domain.BetStatus;

@Service
class SearchBetsHandler implements SearchBets {

  private final BetRepository betRepository;

  SearchBetsHandler(BetRepository betRepository) {
    this.betRepository = betRepository;
  }

  @Override
  public Page<Bet> handle(BetStatus status, Pageable pageable) {
    return betRepository.search(status, pageable);
  }
}
