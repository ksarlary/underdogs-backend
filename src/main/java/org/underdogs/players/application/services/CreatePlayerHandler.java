package org.underdogs.players.application.services;

import org.underdogs.players.application.gateways.PlayerRepository;
import org.underdogs.players.application.models.CreatePlayerRequest;
import org.underdogs.players.application.usecases.CreatePlayer;
import org.underdogs.players.domain.Player;
import org.underdogs.players.domain.PlayerId;
import org.underdogs.shared.DomainIdGenerator;
import org.underdogs.shared.error.BusinessException;
import org.underdogs.teams.application.gateways.TeamRepository;
import org.underdogs.teams.domain.TeamId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class CreatePlayerHandler implements CreatePlayer {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final DomainIdGenerator domainIdGenerator;

    CreatePlayerHandler(
            PlayerRepository playerRepository,
            TeamRepository teamRepository,
            DomainIdGenerator domainIdGenerator
    ) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
        this.domainIdGenerator = domainIdGenerator;
    }

    @Override
    @Transactional
    public PlayerId handle(CreatePlayerRequest request) {
        playerRepository.findByNickname(request.nickname())
                .ifPresent(player -> {
                    throw new BusinessException("PLAYER_ALREADY_EXISTS", "A player with this nickname already exists");
                });

        final var team = teamRepository.findById(new TeamId(request.teamId()))
                .orElseThrow(() -> new BusinessException("TEAM_NOT_FOUND", "Team not found"));

        final var playerId = new PlayerId(domainIdGenerator.generate());

        final var player = Player.create(
                playerId,
                request.nickname(),
                request.fullName(),
                request.role(),
                request.countryCode(),
                team
        );

        playerRepository.save(player);

        return playerId;
    }
}