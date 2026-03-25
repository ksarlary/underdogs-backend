package org.underdogs.tournaments.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.underdogs.tournaments.application.gateways.TournamentRepository;
import org.underdogs.tournaments.domain.Tournament;
import org.underdogs.tournaments.domain.TournamentId;

@Repository
class JpaTournamentRepository implements TournamentRepository {

  private final SpringJpaTournamentRepository jpaRepository;

  JpaTournamentRepository(SpringJpaTournamentRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public void save(Tournament tournament) {
    jpaRepository.save(tournament);
  }

  @Override
  public void delete(Tournament tournament) {
    jpaRepository.delete(tournament);
  }

  @Override
  public Optional<Tournament> findById(TournamentId id) {
    return jpaRepository.findByIdWithMatches(id);
  }

  @Override
  public Optional<Tournament> findByName(String name) {
    return jpaRepository.findByName(name);
  }

  @Override
  public List<Tournament> findAll() {
    return jpaRepository.findAll();
  }
}
