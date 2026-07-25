package org.underdogs.teams.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.underdogs.teams.domain.Game;
import org.underdogs.teams.domain.Team;
import org.underdogs.teams.domain.TeamId;

interface SpringJpaTeamRepository extends JpaRepository<Team, Long> {
  Optional<Team> findById(TeamId id);

  @Query("SELECT t FROM Team t LEFT JOIN FETCH t.players WHERE t.id = :id")
  Optional<Team> findByIdWithPlayers(@Param("id") TeamId id);

  Optional<Team> findByName(String name);

  Optional<Team> findByTag(String tag);

  @Query("SELECT t FROM Team t WHERE (:game IS NULL OR t.game = :game) ORDER BY t.name ASC")
  Page<Team> search(@Param("game") Game game, Pageable pageable);

  long countByGame(Game game);
}
