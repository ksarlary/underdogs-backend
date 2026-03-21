package org.underdogs.players.application.usecases;

import org.underdogs.players.domain.Player;
import org.underdogs.players.domain.PlayerId;

import java.util.Optional;

public interface SearchPlayerById {
    Optional<Player> handle(PlayerId id);
}
