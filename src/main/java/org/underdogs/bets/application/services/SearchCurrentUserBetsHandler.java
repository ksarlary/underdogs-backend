package org.underdogs.bets.application.services;

import java.util.List;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.underdogs.bets.application.gateways.BetRepository;
import org.underdogs.bets.application.usecases.SearchCurrentUserBets;
import org.underdogs.bets.domain.Bet;
import org.underdogs.users.application.usecases.SyncCurrentUser;

@Service
class SearchCurrentUserBetsHandler implements SearchCurrentUserBets {

  private final BetRepository betRepository;
  private final SyncCurrentUser syncCurrentUser;

  SearchCurrentUserBetsHandler(BetRepository betRepository, SyncCurrentUser syncCurrentUser) {
    this.betRepository = betRepository;
    this.syncCurrentUser = syncCurrentUser;
  }

  @Override
  public List<Bet> handle(Jwt jwt) {
    final var user = syncCurrentUser.handle(jwt);
    return betRepository.findByUser(user);
  }
}
