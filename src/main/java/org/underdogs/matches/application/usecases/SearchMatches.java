package org.underdogs.matches.application.usecases;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.underdogs.matches.domain.Match;
import org.underdogs.matches.domain.MatchStatus;
import org.underdogs.teams.domain.Game;

public interface SearchMatches {
  Page<Match> handle(Game game, MatchStatus status, Pageable pageable);
}
