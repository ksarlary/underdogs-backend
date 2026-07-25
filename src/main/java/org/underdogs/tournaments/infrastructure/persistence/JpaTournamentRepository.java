package org.underdogs.tournaments.infrastructure.persistence;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.underdogs.teams.domain.Game;
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
  public Page<Tournament> search(Game game, Pageable pageable) {
    return jpaRepository.search(game, pageable);
  }

  @Override
  public Map<Game, Long> countByGame() {
    return Arrays.stream(Game.values())
        .collect(Collectors.toMap(game -> game, jpaRepository::countByGame));
  }
}
