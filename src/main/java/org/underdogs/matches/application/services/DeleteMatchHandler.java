package org.underdogs.matches.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.underdogs.matches.application.gateways.MatchRepository;
import org.underdogs.matches.application.usecases.DeleteMatch;
import org.underdogs.matches.domain.MatchId;
import org.underdogs.shared.error.BusinessErrorCodes;
import org.underdogs.shared.error.BusinessException;

@Service
class DeleteMatchHandler implements DeleteMatch {

  private final MatchRepository matchRepository;

  DeleteMatchHandler(MatchRepository matchRepository) {
    this.matchRepository = matchRepository;
  }

  @Override
  @Transactional
  public void handle(MatchId id) {
    final var match =
        matchRepository
            .findById(id)
            .orElseThrow(
                () -> new BusinessException(BusinessErrorCodes.MATCH_NOT_FOUND, "Match not found"));

    matchRepository.delete(match);
  }
}
