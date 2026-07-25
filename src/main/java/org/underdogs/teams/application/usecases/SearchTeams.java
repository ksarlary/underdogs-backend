package org.underdogs.teams.application.usecases;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.underdogs.teams.domain.Game;
import org.underdogs.teams.domain.Team;

public interface SearchTeams {
  Page<Team> handle(Game game, Pageable pageable);
}
