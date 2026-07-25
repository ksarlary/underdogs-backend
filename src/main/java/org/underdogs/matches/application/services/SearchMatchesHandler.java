package org.underdogs.matches.application.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.underdogs.matches.application.gateways.MatchRepository;
import org.underdogs.matches.application.usecases.SearchMatches;
import org.underdogs.matches.domain.Match;
import org.underdogs.matches.domain.MatchStatus;
import org.underdogs.teams.domain.Game;

@Service
class SearchMatchesHandler implements SearchMatches {

  private final MatchRepository matchRepository;

  SearchMatchesHandler(MatchRepository matchRepository) {
    this.matchRepository = matchRepository;
  }

  @Override
  public Page<Match> handle(Game game, MatchStatus status, Pageable pageable) {
    return matchRepository.search(game, status, pageable);
  }
}
