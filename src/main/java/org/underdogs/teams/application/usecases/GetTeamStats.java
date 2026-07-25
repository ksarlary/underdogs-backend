package org.underdogs.teams.application.usecases;

import java.util.Map;
import org.underdogs.teams.domain.Game;

public interface GetTeamStats {
  Map<Game, Long> handle();
}
