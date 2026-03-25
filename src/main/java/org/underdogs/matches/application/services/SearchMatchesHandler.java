package org.underdogs.matches.application.services;

import java.util.List;
import org.springframework.stereotype.Service;
import org.underdogs.matches.application.gateways.MatchRepository;
import org.underdogs.matches.application.usecases.SearchMatches;
import org.underdogs.matches.domain.Match;

@Service
class SearchMatchesHandler implements SearchMatches {

  private final MatchRepository matchRepository;

  SearchMatchesHandler(MatchRepository matchRepository) {
    this.matchRepository = matchRepository;
  }

  @Override
  public List<Match> handle() {
    return matchRepository.findAll();
  }
}
