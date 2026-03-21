package org.underdogs.teams.infrastructure.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.underdogs.teams.domain.Team;
import org.underdogs.teams.domain.TeamId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface SpringJpaTeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findById(TeamId id);

    @Query("SELECT t FROM Team t LEFT JOIN FETCH t.players WHERE t.id = :id")
    Optional<Team> findByIdWithPlayers(@Param("id") TeamId id);

    Optional<Team> findByName(String name);
    Optional<Team> findByTag(String tag);
}
