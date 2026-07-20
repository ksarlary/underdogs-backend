package org.underdogs.matches.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.underdogs.matches.application.gateways.MatchRepository;
import org.underdogs.matches.application.usecases.GetMatchResult;
import org.underdogs.matches.domain.Match;
import org.underdogs.matches.domain.MatchId;
import org.underdogs.shared.error.BusinessErrorCodes;
import org.underdogs.shared.error.BusinessException;

@Service
class GetMatchResultHandler implements GetMatchResult {

  private final MatchRepository matchRepository;

  GetMatchResultHandler(MatchRepository matchRepository) {
    this.matchRepository = matchRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public Match handle(String matchId) {
    return matchRepository
        .findById(new MatchId(matchId))
        .orElseThrow(
            () -> new BusinessException(BusinessErrorCodes.MATCH_NOT_FOUND, "Match not found"));
  }
}
