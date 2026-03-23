package org.underdogs.players.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.underdogs.players.application.gateways.PlayerRepository;
import org.underdogs.players.application.models.UpdatePlayerRequest;
import org.underdogs.players.application.usecases.UpdatePlayer;
import org.underdogs.players.domain.PlayerId;
import org.underdogs.shared.error.BusinessErrorCodes;
import org.underdogs.shared.error.BusinessException;
import org.underdogs.teams.application.gateways.TeamRepository;
import org.underdogs.teams.domain.Team;
import org.underdogs.teams.domain.TeamId;

@Service
class UpdatePlayerHandler implements UpdatePlayer {

  private final PlayerRepository playerRepository;
  private final TeamRepository teamRepository;

  UpdatePlayerHandler(PlayerRepository playerRepository, TeamRepository teamRepository) {
    this.playerRepository = playerRepository;
    this.teamRepository = teamRepository;
  }

  @Override
  @Transactional
  public void handle(PlayerId id, UpdatePlayerRequest request) {
    final var player =
        playerRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new BusinessException(BusinessErrorCodes.PLAYER_NOT_FOUND, "Player not found"));

    if (request.nickname() != null && !request.nickname().equals(player.getNickname())) {
      playerRepository
          .findByNickname(request.nickname())
          .filter(existing -> !existing.getId().equals(player.getId()))
          .ifPresent(
              existing -> {
                throw new BusinessException(
                    BusinessErrorCodes.PLAYER_ALREADY_EXISTS,
                    "A player with this nickname already exists");
              });
    }

    Team team = null;
    if (request.teamId() != null && !request.teamId().isBlank()) {
      team =
          teamRepository
              .findById(new TeamId(request.teamId()))
              .orElseThrow(
                  () -> new BusinessException(BusinessErrorCodes.TEAM_NOT_FOUND, "Team not found"));
    }

    player.update(
        request.nickname(), request.fullName(), request.role(), request.countryCode(), team);

    playerRepository.save(player);
  }
}
