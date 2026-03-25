package org.underdogs.tournaments.application.services;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.underdogs.tournaments.application.gateways.TournamentRepository;
import org.underdogs.tournaments.application.usecases.SearchTournamentById;
import org.underdogs.tournaments.domain.Tournament;
import org.underdogs.tournaments.domain.TournamentId;

@Service
class SearchTournamentByIdHandler implements SearchTournamentById {

  private final TournamentRepository tournamentRepository;

  SearchTournamentByIdHandler(TournamentRepository tournamentRepository) {
    this.tournamentRepository = tournamentRepository;
  }

  @Override
  public Optional<Tournament> handle(TournamentId id) {
    return tournamentRepository.findById(id);
  }
}
