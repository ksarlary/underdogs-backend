package org.underdogs.tournaments.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.underdogs.shared.error.BusinessErrorCodes;
import org.underdogs.shared.error.BusinessException;
import org.underdogs.tournaments.application.gateways.TournamentRepository;
import org.underdogs.tournaments.application.usecases.DeleteTournament;
import org.underdogs.tournaments.domain.TournamentId;

@Service
class DeleteTournamentHandler implements DeleteTournament {

  private final TournamentRepository tournamentRepository;

  DeleteTournamentHandler(TournamentRepository tournamentRepository) {
    this.tournamentRepository = tournamentRepository;
  }

  @Override
  @Transactional
  public void handle(TournamentId id) {
    final var tournament =
        tournamentRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new BusinessException(
                        BusinessErrorCodes.TOURNAMENT_NOT_FOUND, "Tournament not found"));

    tournamentRepository.delete(tournament);
  }
}
