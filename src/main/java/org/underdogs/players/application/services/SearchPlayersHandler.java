package org.underdogs.players.application.services;

import org.underdogs.players.application.gateways.PlayerRepository;
import org.underdogs.players.application.usecases.SearchPlayers;
import org.underdogs.players.domain.Player;
import org.springframework.stereotype.Service;

import java.util.List;

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
