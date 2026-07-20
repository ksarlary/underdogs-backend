package org.underdogs.players.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.underdogs.players.domain.Player;
import org.underdogs.players.domain.PlayerId;

interface SpringJpaPlayerRepository extends JpaRepository<Player, Long> {
  Optional<Player> findById(PlayerId id);

  Optional<Player> findByNickname(String nickname);
}
