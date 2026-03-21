package org.underdogs.players.application.gateways;

import org.underdogs.players.domain.Player;
import org.underdogs.players.domain.PlayerId;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository {
    void save(Player player);
    void delete(Player player);
    Optional<Player> findById(PlayerId id);
    Optional<Player> findByNickname(String nickname);
    List<Player> findAll();
}
