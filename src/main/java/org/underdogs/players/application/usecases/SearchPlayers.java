package org.underdogs.players.application.usecases;

import java.util.List;
import org.underdogs.players.domain.Player;

public interface SearchPlayers {
  List<Player> handle();
}
