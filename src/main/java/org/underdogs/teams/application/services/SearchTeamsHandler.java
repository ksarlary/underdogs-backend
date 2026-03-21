package org.underdogs.teams.application.services;

import org.underdogs.teams.application.gateways.TeamRepository;
import org.underdogs.teams.application.usecases.SearchTeams;
import org.underdogs.teams.domain.Team;
import org.springframework.stereotype.Service;

import java.util.List;

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
