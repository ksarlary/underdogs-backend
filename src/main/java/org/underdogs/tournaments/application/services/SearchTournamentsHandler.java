package org.underdogs.tournaments.application.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.underdogs.teams.domain.Game;
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
  public Page<Tournament> handle(Game game, Pageable pageable) {
    return tournamentRepository.search(game, pageable);
  }
}
