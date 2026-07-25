package org.underdogs.bets.application.usecases;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.underdogs.bets.domain.Bet;
import org.underdogs.bets.domain.BetStatus;

public interface SearchBets {
  Page<Bet> handle(BetStatus status, Pageable pageable);
}
