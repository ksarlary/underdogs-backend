package org.underdogs.bets.application.usecases;

import java.util.List;
import org.springframework.security.oauth2.jwt.Jwt;
import org.underdogs.bets.domain.Bet;

public interface SearchCurrentUserBets {
  List<Bet> handle(Jwt jwt);
}
