package org.underdogs.matches.infrastructure.persistence;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.underdogs.matches.application.gateways.MatchRepository;
import org.underdogs.matches.domain.Match;
import org.underdogs.matches.domain.MatchId;
import org.underdogs.matches.domain.MatchStatus;
import org.underdogs.teams.domain.Game;

@Repository
class JpaMatchRepository implements MatchRepository {

  private final SpringJpaMatchRepository jpaRepository;

  JpaMatchRepository(SpringJpaMatchRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public void save(Match match) {
    jpaRepository.save(match);
  }

  @Override
  public void delete(Match match) {
    jpaRepository.delete(match);
  }

  @Override
  public Optional<Match> findById(MatchId id) {
    return jpaRepository.findById(id);
  }

  @Override
  public List<Match> findScheduledMatchesToStart(Instant now) {
    return jpaRepository.findByStatusAndScheduledAtLessThanEqual(MatchStatus.SCHEDULED, now);
  }

  @Override
  public Page<Match> search(Game game, MatchStatus status, Pageable pageable) {
    return jpaRepository.search(game, status, pageable);
  }

  @Override
  public Map<MatchStatus, Long> countByStatus() {
    return Arrays.stream(MatchStatus.values())
        .collect(Collectors.toMap(status -> status, jpaRepository::countByStatus));
  }
}
