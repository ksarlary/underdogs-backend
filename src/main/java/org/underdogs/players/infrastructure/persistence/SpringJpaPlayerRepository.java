package org.underdogs.players.infrastructure.persistence;

import org.underdogs.players.domain.Player;
import org.underdogs.players.domain.PlayerId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface SpringJpaPlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findById(PlayerId id);
    Optional<Player> findByNickname(String nickname);
}
