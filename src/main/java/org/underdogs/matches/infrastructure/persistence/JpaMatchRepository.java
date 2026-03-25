package org.underdogs.matches.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.underdogs.matches.application.gateways.MatchRepository;
import org.underdogs.matches.domain.Match;
import org.underdogs.matches.domain.MatchId;

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
  public List<Match> findAll() {
    return jpaRepository.findAll();
  }
}
