package org.underdogs.teams.application.services;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.underdogs.teams.application.gateways.TeamRepository;
import org.underdogs.teams.application.usecases.SearchTeamById;
import org.underdogs.teams.domain.Team;
import org.underdogs.teams.domain.TeamId;

@Service
class SearchTeamByIdHandler implements SearchTeamById {

  private final TeamRepository teamRepository;

  SearchTeamByIdHandler(TeamRepository teamRepository) {
    this.teamRepository = teamRepository;
  }

  @Override
  public Optional<Team> handle(TeamId id) {
    return teamRepository.findById(id);
  }
}
