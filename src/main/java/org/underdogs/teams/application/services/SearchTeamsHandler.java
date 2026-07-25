package org.underdogs.teams.application.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.underdogs.teams.application.gateways.TeamRepository;
import org.underdogs.teams.application.usecases.SearchTeams;
import org.underdogs.teams.domain.Game;
import org.underdogs.teams.domain.Team;

@Service
class SearchTeamsHandler implements SearchTeams {

  private final TeamRepository teamRepository;

  SearchTeamsHandler(TeamRepository teamRepository) {
    this.teamRepository = teamRepository;
  }

  @Override
  public Page<Team> handle(Game game, Pageable pageable) {
    return teamRepository.search(game, pageable);
  }
}
