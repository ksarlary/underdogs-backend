package org.underdogs.matches.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.underdogs.matches.domain.Match;
import org.underdogs.matches.domain.MatchId;
import org.underdogs.matches.domain.MatchStatus;

interface SpringJpaMatchRepository extends JpaRepository<Match, Long> {
  Optional<Match> findById(MatchId id);

  List<Match> findByStatusAndScheduledAtLessThanEqual(
      MatchStatus status, LocalDateTime scheduledAt);
}
