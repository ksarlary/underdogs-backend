package org.underdogs.bets.application.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.underdogs.bets.application.gateways.BetRepository;
import org.underdogs.bets.application.usecases.SearchCurrentUserBets;
import org.underdogs.bets.domain.Bet;
import org.underdogs.bets.domain.BetStatus;
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
  public Page<Bet> handle(Jwt jwt, BetStatus status, Pageable pageable) {
    final var user = syncCurrentUser.handle(jwt);
    return betRepository.findByUser(user, status, pageable);
  }
}
