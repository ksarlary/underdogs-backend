package org.underdogs.tournaments.application.usecases;

import java.util.Map;
import org.underdogs.teams.domain.Game;

public interface GetTournamentStats {
  Map<Game, Long> handle();
}
