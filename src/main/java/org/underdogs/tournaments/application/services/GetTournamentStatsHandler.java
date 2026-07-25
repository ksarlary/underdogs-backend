package org.underdogs.tournaments.application.services;

import java.util.Map;
import org.springframework.stereotype.Service;
import org.underdogs.teams.domain.Game;
import org.underdogs.tournaments.application.gateways.TournamentRepository;
import org.underdogs.tournaments.application.usecases.GetTournamentStats;

@Service
class GetTournamentStatsHandler implements GetTournamentStats {

  private final TournamentRepository tournamentRepository;

  GetTournamentStatsHandler(TournamentRepository tournamentRepository) {
    this.tournamentRepository = tournamentRepository;
  }

  @Override
  public Map<Game, Long> handle() {
    return tournamentRepository.countByGame();
  }
}
