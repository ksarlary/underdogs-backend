package org.underdogs.bets.application.usecases;

import java.util.List;
import org.underdogs.bets.domain.Bet;

public interface SearchBets {
  List<Bet> handle();
}
