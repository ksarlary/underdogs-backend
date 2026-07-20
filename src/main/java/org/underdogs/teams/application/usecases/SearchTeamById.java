package org.underdogs.teams.application.usecases;

import java.util.Optional;
import org.underdogs.teams.domain.Team;
import org.underdogs.teams.domain.TeamId;

public interface SearchTeamById {
  Optional<Team> handle(TeamId id);
}
