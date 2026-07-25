package org.underdogs.teams.application.gateways;

import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.underdogs.teams.domain.Game;
import org.underdogs.teams.domain.Team;
import org.underdogs.teams.domain.TeamId;

public interface TeamRepository {
  void save(Team team);

  void delete(Team team);

  Optional<Team> findById(TeamId id);

  Optional<Team> findByName(String name);

  Optional<Team> findByTag(String tag);

  Page<Team> search(Game game, Pageable pageable);

  Map<Game, Long> countByGame();
}
