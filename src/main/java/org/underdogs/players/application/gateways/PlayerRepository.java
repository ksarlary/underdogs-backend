package org.underdogs.players.application.gateways;

import java.util.List;
import java.util.Optional;
import org.underdogs.players.domain.Player;
import org.underdogs.players.domain.PlayerId;

public interface PlayerRepository {
  void save(Player player);

  void delete(Player player);

  Optional<Player> findById(PlayerId id);

  Optional<Player> findByNickname(String nickname);

  List<Player> findAll();
}
