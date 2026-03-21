package org.underdogs.teams.application.usecases;

import org.underdogs.teams.domain.Team;
import org.underdogs.teams.domain.TeamId;

import java.util.Optional;

public interface SearchTeamById {
    Optional<Team> handle(TeamId id);
}
