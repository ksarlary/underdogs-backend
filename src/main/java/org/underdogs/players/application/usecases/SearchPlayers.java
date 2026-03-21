package org.underdogs.players.application.usecases;

import org.underdogs.players.domain.Player;

import java.util.List;

public interface SearchPlayers {
    List<Player> handle();
}
