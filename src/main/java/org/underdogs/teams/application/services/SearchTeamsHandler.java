package org.underdogs.teams.application.services;

import java.util.List;
import org.springframework.stereotype.Service;
import org.underdogs.teams.application.gateways.TeamRepository;
import org.underdogs.teams.application.usecases.SearchTeams;
import org.underdogs.teams.domain.Team;

@Service
class SearchTeamsHandler implements SearchTeams {

  private final TeamRepository teamRepository;

  SearchTeamsHandler(TeamRepository teamRepository) {
    this.teamRepository = teamRepository;
  }

  @Override
  public List<Team> handle() {
    return teamRepository.findAll();
  }
}
