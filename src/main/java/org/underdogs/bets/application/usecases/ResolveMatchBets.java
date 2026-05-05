package org.underdogs.bets.application.usecases;

import org.underdogs.matches.domain.Match;

public interface ResolveMatchBets {
  void handle(Match match);
}
