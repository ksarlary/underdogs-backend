package org.underdogs.players.application.services;

import java.util.List;
import org.springframework.stereotype.Service;
import org.underdogs.players.application.gateways.PlayerRepository;
import org.underdogs.players.application.usecases.SearchPlayers;
import org.underdogs.players.domain.Player;

@Service
class SearchPlayersHandler implements SearchPlayers {

  private final PlayerRepository playerRepository;

  SearchPlayersHandler(PlayerRepository playerRepository) {
    this.playerRepository = playerRepository;
  }

  @Override
  public List<Player> handle() {
    return playerRepository.findAll();
  }
}
