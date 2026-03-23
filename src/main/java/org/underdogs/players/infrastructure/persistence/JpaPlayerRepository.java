package org.underdogs.players.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.underdogs.players.application.gateways.PlayerRepository;
import org.underdogs.players.domain.Player;
import org.underdogs.players.domain.PlayerId;

@Repository
class JpaPlayerRepository implements PlayerRepository {

  private final SpringJpaPlayerRepository jpaRepository;

  JpaPlayerRepository(SpringJpaPlayerRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public void save(Player player) {
    jpaRepository.save(player);
  }

  @Override
  public Optional<Player> findById(PlayerId id) {
    return jpaRepository.findById(id);
  }

  @Override
  public Optional<Player> findByNickname(String nickname) {
    return jpaRepository.findByNickname(nickname);
  }

  @Override
  public List<Player> findAll() {
    return jpaRepository.findAll();
  }

  @Override
  public void delete(Player player) {
    jpaRepository.delete(player);
  }
}
