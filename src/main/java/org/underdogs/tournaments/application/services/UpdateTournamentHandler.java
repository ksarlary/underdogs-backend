package org.underdogs.tournaments.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.underdogs.shared.error.BusinessErrorCodes;
import org.underdogs.shared.error.BusinessException;
import org.underdogs.tournaments.application.gateways.TournamentRepository;
import org.underdogs.tournaments.application.models.UpdateTournamentRequest;
import org.underdogs.tournaments.application.usecases.UpdateTournament;
import org.underdogs.tournaments.domain.TournamentId;

@Service
class UpdateTournamentHandler implements UpdateTournament {

  private final TournamentRepository tournamentRepository;

  UpdateTournamentHandler(TournamentRepository tournamentRepository) {
    this.tournamentRepository = tournamentRepository;
  }

  @Override
  @Transactional
  public void handle(TournamentId id, UpdateTournamentRequest request) {
    final var tournament =
        tournamentRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new BusinessException(
                        BusinessErrorCodes.TOURNAMENT_NOT_FOUND, "Tournament not found"));

    if (request.name() != null && !request.name().equals(tournament.getName())) {
      tournamentRepository
          .findByName(request.name())
          .filter(existing -> !existing.getId().equals(tournament.getId()))
          .ifPresent(
              existing -> {
                throw new BusinessException(
                    BusinessErrorCodes.TOURNAMENT_ALREADY_EXISTS,
                    "A tournament with this name already exists");
              });
    }

    tournament.update(request.name(), request.game(), request.startDate(), request.endDate());

    tournamentRepository.save(tournament);
  }
}
