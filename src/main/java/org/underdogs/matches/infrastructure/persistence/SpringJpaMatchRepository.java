package org.underdogs.matches.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.underdogs.matches.domain.Match;
import org.underdogs.matches.domain.MatchId;

interface SpringJpaMatchRepository extends JpaRepository<Match, Long> {
  Optional<Match> findById(MatchId id);
}
