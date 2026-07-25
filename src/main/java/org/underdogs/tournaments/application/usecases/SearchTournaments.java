package org.underdogs.tournaments.application.usecases;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.underdogs.teams.domain.Game;
import org.underdogs.tournaments.domain.Tournament;

public interface SearchTournaments {
  Page<Tournament> handle(Game game, Pageable pageable);
}
