package org.underdogs.players.application.services;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.underdogs.players.application.gateways.PlayerRepository;
import org.underdogs.players.application.usecases.SearchPlayerById;
import org.underdogs.players.domain.Player;
import org.underdogs.players.domain.PlayerId;

@Service
class SearchPlayerByIdHandler implements SearchPlayerById {

  private final PlayerRepository playerRepository;

  SearchPlayerByIdHandler(PlayerRepository playerRepository) {
    this.playerRepository = playerRepository;
  }

  @Override
  public Optional<Player> handle(PlayerId id) {
    return playerRepository.findById(id);
  }
}
