package org.underdogs.bets.application.services;

import java.util.List;
import org.springframework.stereotype.Service;
import org.underdogs.bets.application.gateways.BetRepository;
import org.underdogs.bets.application.usecases.SearchBets;
import org.underdogs.bets.domain.Bet;

@Service
class SearchBetsHandler implements SearchBets {

  private final BetRepository betRepository;

  SearchBetsHandler(BetRepository betRepository) {
    this.betRepository = betRepository;
  }

  @Override
  public List<Bet> handle() {
    return betRepository.findAll();
  }
}
