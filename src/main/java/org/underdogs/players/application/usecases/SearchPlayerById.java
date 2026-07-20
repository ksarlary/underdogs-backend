package org.underdogs.players.application.usecases;

import java.util.Optional;
import org.underdogs.players.domain.Player;
import org.underdogs.players.domain.PlayerId;

public interface SearchPlayerById {
  Optional<Player> handle(PlayerId id);
}
