package org.underdogs.players.application.services;

import org.underdogs.players.application.gateways.PlayerRepository;
import org.underdogs.players.application.usecases.SearchPlayerById;
import org.underdogs.players.domain.Player;
import org.underdogs.players.domain.PlayerId;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
