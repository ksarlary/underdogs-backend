package org.underdogs.teams.application.usecases;

import java.util.List;
import org.underdogs.teams.domain.Team;

public interface SearchTeams {
  List<Team> handle();
}
