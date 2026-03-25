package org.underdogs.tournaments.application.services;

import java.util.List;
import org.springframework.stereotype.Service;
import org.underdogs.tournaments.application.gateways.TournamentRepository;
import org.underdogs.tournaments.application.usecases.SearchTournaments;
import org.underdogs.tournaments.domain.Tournament;

@Service
class SearchTournamentsHandler implements SearchTournaments {

  private final TournamentRepository tournamentRepository;

  SearchTournamentsHandler(TournamentRepository tournamentRepository) {
    this.tournamentRepository = tournamentRepository;
  }

  @Override
  public List<Tournament> handle() {
    return tournamentRepository.findAll();
  }
}
