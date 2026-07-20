package org.underdogs.teams.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.underdogs.teams.application.gateways.TeamRepository;
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
  public List<Team> findAll() {
    return jpaRepository.findAll();
  }

  @Override
  public void delete(Team team) {
    jpaRepository.delete(team);
  }
}
