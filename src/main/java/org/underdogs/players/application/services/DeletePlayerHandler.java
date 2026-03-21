package org.underdogs.players.application.services;

import org.underdogs.players.application.gateways.PlayerRepository;
import org.underdogs.players.application.usecases.DeletePlayer;
import org.underdogs.players.domain.PlayerId;
import org.underdogs.shared.error.BusinessErrorCodes;
import org.underdogs.shared.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class DeletePlayerHandler implements DeletePlayer {

    private final PlayerRepository playerRepository;

    DeletePlayerHandler(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    @Transactional
    public void handle(PlayerId id) {
        final var player = playerRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        BusinessErrorCodes.PLAYER_NOT_FOUND,
                        "Player not found"
                ));

        playerRepository.delete(player);
    }
}
