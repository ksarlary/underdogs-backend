package org.underdogs.tournaments.application.gateways;

import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.underdogs.teams.domain.Game;
import org.underdogs.tournaments.domain.Tournament;
import org.underdogs.tournaments.domain.TournamentId;

public interface TournamentRepository {
  void save(Tournament tournament);

  void delete(Tournament tournament);

  Optional<Tournament> findById(TournamentId id);

  Optional<Tournament> findByName(String name);

  Page<Tournament> search(Game game, Pageable pageable);

  Map<Game, Long> countByGame();
}
