package org.underdogs.tournaments.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.underdogs.tournaments.domain.Tournament;
import org.underdogs.tournaments.domain.TournamentId;

interface SpringJpaTournamentRepository extends JpaRepository<Tournament, Long> {
  Optional<Tournament> findById(TournamentId id);

  Optional<Tournament> findByName(String name);

  @Query(
      """
           SELECT DISTINCT t
           FROM Tournament t
           LEFT JOIN FETCH t.matches m
           LEFT JOIN FETCH m.team1
           LEFT JOIN FETCH m.team2
           WHERE t.id = :id
           """)
  Optional<Tournament> findByIdWithMatches(@Param("id") TournamentId id);
}
