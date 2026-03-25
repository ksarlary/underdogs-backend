package org.underdogs.matches.application.services;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.underdogs.matches.application.gateways.MatchRepository;
import org.underdogs.matches.application.usecases.SearchMatchById;
import org.underdogs.matches.domain.Match;
import org.underdogs.matches.domain.MatchId;

@Service
class SearchMatchByIdHandler implements SearchMatchById {

  private final MatchRepository matchRepository;

  SearchMatchByIdHandler(MatchRepository matchRepository) {
    this.matchRepository = matchRepository;
  }

  @Override
  public Optional<Match> handle(MatchId id) {
    return matchRepository.findById(id);
  }
}
