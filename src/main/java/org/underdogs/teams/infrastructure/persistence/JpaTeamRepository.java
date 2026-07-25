package org.underdogs.teams.infrastructure.persistence;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.underdogs.teams.application.gateways.TeamRepository;
import org.underdogs.teams.domain.Game;
import org.underdogs.teams.domain.Team;
import org.underdogs.teams.domain.TeamId;

@Repository
class JpaTeamRepository implements TeamRepository {

  private final SpringJpaTeamRepository jpaRepository;

  JpaTeamRepository(SpringJpaTeamRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public void save(Team team) {
    jpaRepository.save(team);
  }

  @Override
  public Optional<Team> findById(TeamId id) {
    return jpaRepository.findByIdWithPlayers(id);
  }

  @Override
  public Optional<Team> findByName(String name) {
    return jpaRepository.findByName(name);
  }

  @Override
  public Optional<Team> findByTag(String tag) {
    return jpaRepository.findByTag(tag);
  }

  @Override
  public void delete(Team team) {
    jpaRepository.delete(team);
  }

  @Override
  public Page<Team> search(Game game, Pageable pageable) {
    return jpaRepository.search(game, pageable);
  }

  @Override
  public Map<Game, Long> countByGame() {
    return Arrays.stream(Game.values())
        .collect(Collectors.toMap(game -> game, jpaRepository::countByGame));
  }
}
