package org.underdogs.bets.application.usecases;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.underdogs.bets.domain.Bet;
import org.underdogs.bets.domain.BetStatus;

public interface SearchCurrentUserBets {
  Page<Bet> handle(Jwt jwt, BetStatus status, Pageable pageable);
}
