package org.underdogs.tournaments.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.underdogs.shared.DomainIdGenerator;
import org.underdogs.shared.error.BusinessErrorCodes;
import org.underdogs.shared.error.BusinessException;
import org.underdogs.tournaments.application.gateways.TournamentRepository;
import org.underdogs.tournaments.application.models.CreateTournamentRequest;
import org.underdogs.tournaments.application.usecases.CreateTournament;
import org.underdogs.tournaments.domain.Tournament;
import org.underdogs.tournaments.domain.TournamentId;

@Service
class CreateTournamentHandler implements CreateTournament {

  private final TournamentRepository tournamentRepository;
  private final DomainIdGenerator domainIdGenerator;

  CreateTournamentHandler(
      TournamentRepository tournamentRepository, DomainIdGenerator domainIdGenerator) {
    this.tournamentRepository = tournamentRepository;
    this.domainIdGenerator = domainIdGenerator;
  }

  @Override
  @Transactional
  public TournamentId handle(CreateTournamentRequest request) {
    tournamentRepository
        .findByName(request.name())
        .ifPresent(
            existing -> {
              throw new BusinessException(
                  BusinessErrorCodes.TOURNAMENT_ALREADY_EXISTS,
                  "A tournament with this name already exists");
            });

    TournamentId tournamentId = new TournamentId(domainIdGenerator.generate());

    Tournament tournament =
        Tournament.create(
            tournamentId, request.name(), request.game(), request.startDate(), request.endDate());

    tournamentRepository.save(tournament);
    return tournamentId;
  }
}
