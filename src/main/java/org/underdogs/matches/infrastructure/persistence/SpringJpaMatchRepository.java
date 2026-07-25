package org.underdogs.matches.infrastructure.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.underdogs.matches.domain.Match;
import org.underdogs.matches.domain.MatchId;
import org.underdogs.matches.domain.MatchStatus;
import org.underdogs.teams.domain.Game;

interface SpringJpaMatchRepository extends JpaRepository<Match, Long> {
  Optional<Match> findById(MatchId id);

  List<Match> findByStatusAndScheduledAtLessThanEqual(MatchStatus status, Instant scheduledAt);

  @Query(
      """
           SELECT m FROM Match m
           WHERE (:game IS NULL OR m.game = :game)
             AND (:status IS NULL OR m.status = :status)
           ORDER BY
             CASE m.status
               WHEN org.underdogs.matches.domain.MatchStatus.LIVE THEN 0
               WHEN org.underdogs.matches.domain.MatchStatus.SCHEDULED THEN 1
               ELSE 2
             END,
             m.scheduledAt ASC
           """)
  Page<Match> search(
      @Param("game") Game game, @Param("status") MatchStatus status, Pageable pageable);

  long countByStatus(MatchStatus status);
}
