package org.underdogs.bets.application.usecases;

import org.underdogs.bets.application.models.MatchOddsResponse;
import org.underdogs.matches.domain.MatchId;

public interface GetMatchOdds {
  MatchOddsResponse handle(MatchId matchId);
}
