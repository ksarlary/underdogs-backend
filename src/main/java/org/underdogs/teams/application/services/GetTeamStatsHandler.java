package org.underdogs.teams.application.services;

import java.util.Map;
import org.springframework.stereotype.Service;
import org.underdogs.teams.application.gateways.TeamRepository;
import org.underdogs.teams.application.usecases.GetTeamStats;
import org.underdogs.teams.domain.Game;

@Service
class GetTeamStatsHandler implements GetTeamStats {

  private final TeamRepository teamRepository;

  GetTeamStatsHandler(TeamRepository teamRepository) {
    this.teamRepository = teamRepository;
  }

  @Override
  public Map<Game, Long> handle() {
    return teamRepository.countByGame();
  }
}
