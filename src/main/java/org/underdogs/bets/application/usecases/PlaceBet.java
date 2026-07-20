package org.underdogs.bets.application.usecases;

import org.springframework.security.oauth2.jwt.Jwt;
import org.underdogs.bets.application.models.PlaceBetRequest;
import org.underdogs.bets.domain.BetId;

public interface PlaceBet {
  BetId handle(Jwt jwt, PlaceBetRequest request);
}
