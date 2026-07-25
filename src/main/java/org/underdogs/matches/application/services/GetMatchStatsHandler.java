package org.underdogs.matches.application.services;

import java.util.Map;
import org.springframework.stereotype.Service;
import org.underdogs.matches.application.gateways.MatchRepository;
import org.underdogs.matches.application.usecases.GetMatchStats;
import org.underdogs.matches.domain.MatchStatus;

@Service
class GetMatchStatsHandler implements GetMatchStats {

  private final MatchRepository matchRepository;

  GetMatchStatsHandler(MatchRepository matchRepository) {
    this.matchRepository = matchRepository;
  }

  @Override
  public Map<MatchStatus, Long> handle() {
    return matchRepository.countByStatus();
  }
}
